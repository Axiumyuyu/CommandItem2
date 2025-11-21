package me.axiumyu.commandItem2

import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType.*
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object PDCUtils {

    @JvmField
    val plugin: JavaPlugin = getPlugin(CommandItem2::class.java)

    // Keys for storing data in PersistentDataContainer
    @JvmField
    val KEY_ID: NamespacedKey = NamespacedKey(plugin, "item_id")

    @JvmField
    val KEY_COMMANDS: NamespacedKey = NamespacedKey(plugin, "item_commands")

    @JvmField
    val KEY_PERM_REQ: NamespacedKey = NamespacedKey(plugin, "item_perm_req")

    @JvmField
    val KEY_COOLDOWN: NamespacedKey = NamespacedKey(plugin, "item_cooldown")

    @JvmField
    val KEY_CONSUME: NamespacedKey = NamespacedKey(plugin, "item_consume")

    /**
     * Applies the full set of ItemData to an ItemStack's PersistentDataContainer.
     */
    @JvmStatic
    fun applyDataToItemStack(itemStack: ItemStack, data: ItemData) {
        itemStack.editMeta {
            itemStack.addUnsafeEnchantments(data.enchantments)
            val pdc = it.persistentDataContainer
            pdc.set(KEY_ID, STRING, data.id)
            pdc.set(KEY_COMMANDS, STRING, data.commands.joinToString("\n"))
            pdc.set(KEY_PERM_REQ, BOOLEAN, data.permissionRequired)
            pdc.set(KEY_COOLDOWN, LONG, data.cooldown)
            pdc.set(KEY_CONSUME, BOOLEAN, data.consume)

            // Also apply visual properties
            it.itemName(data.name)
            it.displayName(data.name)
            it.lore(data.lore)
            // Clear existing enchants before adding new ones
            it.removeEnchantments()
            data.enchantments.forEach { (enchant, level) -> it.addEnchant(enchant, level, true) }
        }
    }

    /**
     * Reads the ItemData from an ItemStack's PersistentDataContainer.
     * Returns null if the item is not a special item (missing ID).
     */
    @JvmStatic
    fun readDataFromItemStack(itemStack: ItemStack): ItemData? {
        val meta = itemStack.itemMeta ?: return null
        val pdc = meta.persistentDataContainer

        val id = pdc.get(KEY_ID, STRING) ?: return null

        return ItemData(
            id = id,
            material = itemStack.type,
            name = Component.text(itemStack.type.name),
            lore = itemStack.lore() ?: listOf(),
            enchantments = itemStack.enchants,
            commands = pdc.get(KEY_COMMANDS, STRING)?.split("\n") ?: emptyList(),
            permissionRequired = pdc.get(KEY_PERM_REQ, BOOLEAN) == true,
            cooldown = pdc.get(KEY_COOLDOWN, LONG) ?: 0L,
            consume = pdc.get(KEY_CONSUME, BOOLEAN) == true
        )
    }

    @JvmStatic
    fun addExtraInfo(itemStack: ItemStack, data: ItemData) {
        itemStack.editMeta {
            val cd = data.cooldown
            if (cd != 0L) {
                val cdComponent = it.useCooldown
                cdComponent.cooldownSeconds = cd.toFloat()
                it.setUseCooldown(cdComponent)
            }
            val lore = it.lore() ?: mutableListOf()
            lore.add(mm.deserialize("<gray> --------</gray>"))
            lore.add(mm.deserialize("<gray>冷却时间:${data.cooldown}s</gray>"))
            lore.add(mm.deserialize("<gray>是否需要权限:${data.permissionRequired}</gray>"))
            lore.add(mm.deserialize("<gray>使用后是否消耗:${data.consume}</gray>"))
            it.lore(lore)
        }
    }

}
