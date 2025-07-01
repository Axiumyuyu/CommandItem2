package me.axiumyu.commandItem2

import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

object PDCUtils {

    private lateinit var plugin: JavaPlugin

    // Keys for storing data in PersistentDataContainer
    lateinit var KEY_ID: NamespacedKey

    lateinit var KEY_COMMANDS: NamespacedKey
    lateinit var KEY_PERM_REQ: NamespacedKey
    lateinit var KEY_COOLDOWN: NamespacedKey
    lateinit var KEY_CONSUME: NamespacedKey

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin
        KEY_ID = NamespacedKey(plugin, "item_id")
        KEY_COMMANDS = NamespacedKey(plugin, "item_commands")
        KEY_PERM_REQ = NamespacedKey(plugin, "item_perm_req")
        KEY_COOLDOWN = NamespacedKey(plugin, "item_cooldown")
        KEY_CONSUME = NamespacedKey(plugin, "item_consume")
    }

    /**
     * Applies the full set of ItemData to an ItemStack's PersistentDataContainer.
     */
    fun applyDataToItemStack(itemStack: ItemStack, data: ItemData) {
        itemStack.editMeta { meta ->
            itemStack.addUnsafeEnchantments(data.enchantments)
            val pdc = meta.persistentDataContainer
            pdc.set(KEY_ID, PersistentDataType.STRING, data.id)
            pdc.set(KEY_COMMANDS, PersistentDataType.STRING, data.commands.joinToString("\n"))
            pdc.set(KEY_PERM_REQ, PersistentDataType.BYTE, if (data.permissionRequired) 1 else 0)
            pdc.set(KEY_COOLDOWN, PersistentDataType.LONG, data.cooldown)
            pdc.set(KEY_CONSUME, PersistentDataType.BYTE, if (data.consume) 1 else 0)

            // Also apply visual properties
            meta.displayName(data.name)
            meta.lore(data.lore)
            // Clear existing enchants before adding new ones
            meta.enchants.keys.forEach { meta.removeEnchant(it) }
            data.enchantments.forEach { (enchant, level) -> meta.addEnchant(enchant, level, true) }
        }
    }

    /**
     * Reads the ItemData from an ItemStack's PersistentDataContainer.
     * Returns null if the item is not a special item (missing ID).
     */
    fun readDataFromItemStack(itemStack: ItemStack): ItemData? {
        val meta = itemStack.itemMeta ?: return null
        val pdc = meta.persistentDataContainer

        val id = pdc.get(KEY_ID, PersistentDataType.STRING) ?: return null

        return ItemData(
            id = id,
            material = itemStack.type,
            name = Component.text(itemStack.type.name),
            lore = itemStack.lore() ?: listOf(),
            enchantments =  itemStack.enchants,
            commands = pdc.get(KEY_COMMANDS, PersistentDataType.STRING)?.split("\n") ?: emptyList(),
            permissionRequired = pdc.get(KEY_PERM_REQ, PersistentDataType.BYTE) == 1.toByte(),
            cooldown = pdc.get(KEY_COOLDOWN, PersistentDataType.LONG) ?: 0L,
            consume = pdc.get(KEY_CONSUME, PersistentDataType.BYTE) == 1.toByte()
        )
    }

    fun addExtraInfo(itemStack: ItemStack, data: ItemData) {
        itemStack.editMeta {
            val lore = it.lore() ?: mutableListOf()
            lore.add(mm.deserialize("<gray> --------</gray>"))
            lore.add(mm.deserialize("<gray>冷却时间:${data.cooldown}s</gray>"))
            lore.add(mm.deserialize("<gray>是否需要权限:${data.permissionRequired}</gray>"))
            lore.add(mm.deserialize("<gray>使用后是否消耗:${data.consume}</gray>"))
            it.lore(lore)
        }
    }

}
