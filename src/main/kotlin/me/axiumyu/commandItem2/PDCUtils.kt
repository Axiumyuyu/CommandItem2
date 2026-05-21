package me.axiumyu.commandItem2

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.UseCooldown
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import jdk.jfr.DataAmount
import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import me.axiumyu.commandItem2.CommandItem2.Companion.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.getServer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.persistence.PersistentDataType.*
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object PDCUtils {


    const val PDC_NAMESPACE = "ci"

    // Keys for storing data in PersistentDataContainer
    val KEY_ID: NamespacedKey = NamespacedKey(PDC_NAMESPACE, "item_id")

    val KEY_COMMANDS: NamespacedKey = NamespacedKey(PDC_NAMESPACE, "item_commands")

    val KEY_PERM_REQ: NamespacedKey = NamespacedKey(PDC_NAMESPACE, "item_perm_req")

    val KEY_COOLDOWN: NamespacedKey = NamespacedKey(PDC_NAMESPACE, "item_cooldown")

    val KEY_CONSUME: NamespacedKey = NamespacedKey(PDC_NAMESPACE, "item_consume")

    /**
     * Applies the full set of ItemData to an ItemStack's PersistentDataContainer.
     */
    fun applyDataToItemStack(itemStack: ItemStack, data: ItemData) {

        itemStack.editMeta {
            val pdc = it.persistentDataContainer
            pdc.set(KEY_ID, STRING, data.id)
            pdc.set(KEY_COMMANDS, LIST.strings(), data.commands)
            pdc.set(KEY_PERM_REQ, BOOLEAN, data.permissionRequired)
            pdc.set(KEY_COOLDOWN, LONG, data.cooldown)
            pdc.set(KEY_CONSUME, BOOLEAN, data.consume)

            // Also apply visual properties
            it.itemName(data.name)
            it.displayName(data.name)
            it.lore(data.lore)
            // Clear existing enchants before adding new ones
            it.removeEnchantments()
        }
        itemStack.addUnsafeEnchantments(data.enchantments)
    }

    /**
     * Reads the ItemData from an ItemStack's PersistentDataContainer.
     * Returns null if the item is not a special item (missing ID).
     */
    fun readDataFromItemStack(itemStack: ItemStack): ItemData? {
        val meta = itemStack.itemMeta ?: return null
        val pdc = meta.persistentDataContainer

        val id = pdc.get(KEY_ID, STRING) ?: return null

        return ItemData(
            id = id,
            material = itemStack.type,
            name = Component.text(itemStack.type.name),
            lore = itemStack.lore() ?: listOf(),
            enchantments = itemStack.enchantments,
            commands = pdc.get(KEY_COMMANDS, LIST.strings()) ?: emptyList(),
            permissionRequired = pdc.get(KEY_PERM_REQ, BOOLEAN) == true,
            cooldown = pdc.get(KEY_COOLDOWN, LONG) ?: 0L,
            consume = pdc.get(KEY_CONSUME, BOOLEAN) == true
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
