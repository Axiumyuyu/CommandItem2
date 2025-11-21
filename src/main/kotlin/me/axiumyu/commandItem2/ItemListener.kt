package me.axiumyu.commandItem2


import me.axiumyu.commandItem2.CommandItem2.Companion.PERM_USE
import me.axiumyu.commandItem2.CommandItem2.Companion.isStrict
import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import me.axiumyu.commandItem2.CommandItem2.Companion.setPAPI
import me.axiumyu.commandItem2.PDCUtils.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap



object ItemListener : Listener {

    // <Player UUID, <Item ID, Cooldown End Time (ms)>>
    val cooldowns: ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> = ConcurrentHashMap()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action.isLeftClick) return

        val item = event.item ?: return
        val pdcData = PDCUtils.readDataFromItemStack(item) ?: return

        val player = event.player
        val itemId = pdcData.id

        // Prevent using items by placing them (e.g., spawn eggs)
//        if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock?.type?.isInteractable == false) {
        event.isCancelled = true
//        }

        val effectiveData = getEffectiveData(pdcData)
        if (effectiveData == null) {
            player.sendMessage(mm.deserialize("<red>This command item ('$itemId') is outdated or misconfigured."))
            return
        }

        // 1. Permission Check
        if (effectiveData.permissionRequired && !player.hasPermission("$PERM_USE.${effectiveData.id}")) {
            player.sendMessage(mm.deserialize("<red>You don't have permission to use this item."))
            return
        }

        // 2. Cooldown Check
        val remainingCooldown = getCooldown(player.uniqueId, effectiveData.id)
        if (remainingCooldown > 0) {
            player.sendActionBar(mm.deserialize("<red>You must wait <yellow>${"%.1f".format(remainingCooldown / 1000.0)}s<red> before using this again.")) // Using ActionBar for less intrusive message
            return
        }

        // 3. Execute Commands
        effectiveData.commands.forEach {
            val parsedCmd = setPAPI(player, it)
            plugin.server.dispatchCommand(plugin.server.consoleSender, parsedCmd)
        }

        // 4. Set Cooldown
        if (effectiveData.cooldown > 0) {
            setCooldown(player.uniqueId, effectiveData.id, effectiveData.cooldown)
        }

        // 5. Consume Item
        if (effectiveData.consume) {
            item.amount -= 1
        }
    }

    private fun getEffectiveData(pdcData: ItemData): ItemData? {
        return if (isStrict) {
            // Strict mode: Always use data from config
            ItemManager.getItemData(pdcData.id)
        } else {
            // Non-strict mode: Use PDC data, fallback to config
            val configData = ItemManager.getItemData(pdcData.id)
            configData?.let {
                // This is a simple fallback. A more complex merge could be implemented here
                // if you wanted to mix-and-match properties. For now, we trust the PDC
                // if it exists, otherwise we assume it's an old item and needs fresh config data.
                pdcData.copy(
                    commands = pdcData.commands.ifEmpty { it.commands },
                    cooldown = if (pdcData.cooldown > 0) pdcData.cooldown else it.cooldown,
                    //for boolean type just use pdcData
                    consume = pdcData.consume, // consume is a good candidate to always trust the PDC
                    permissionRequired = pdcData.permissionRequired
                )
            } ?: pdcData // If config no longer exists, just use what's on the item
        }
    }

    private fun setCooldown(uuid: UUID, itemId: String, seconds: Long) {
        if (seconds <= 0) return
        val endTime = System.currentTimeMillis() + seconds * 1000
        cooldowns.computeIfAbsent(uuid) { ConcurrentHashMap() }[itemId] = endTime
    }

    fun getCooldown(uuid: UUID, itemId: String): Long {
        val endTime = cooldowns[uuid]?.get(itemId) ?: return 0
        val remaining = endTime - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0
    }
}
