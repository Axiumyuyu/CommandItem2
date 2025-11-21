package me.axiumyu.commandItem2

import io.papermc.paper.registry.RegistryKey.ENCHANTMENT
import me.axiumyu.commandItem2.CommandItem2.Companion.PERM_USE
import me.axiumyu.commandItem2.CommandItem2.Companion.getRegistry
import me.axiumyu.commandItem2.CommandItem2.Companion.isStrict
import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import me.axiumyu.commandItem2.CommandItem2.Companion.namespacedKey
import me.axiumyu.commandItem2.PDCUtils.plugin
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.Permission

object ItemManager {

    private val specialItems = mutableMapOf<String, ItemData>()

    fun loadItems() {

        specialItems.clear()
        unregisterUsePermissions()

        val config = plugin.config

        isStrict = config.getBoolean("strict", false)
        val itemsSection = config.getConfigurationSection("items") ?: return

        for (id in itemsSection.getKeys(false)) {
            val path = "items.$id"
            try {

                //material name
                val materialName = config.getString("$path.material")?.uppercase()
                val material = materialName?.let { Material.getMaterial(it) }
                if (material == null) {
                    plugin.logger.warning("Invalid material '$materialName' for item '$id'. Skipping.")
                    continue
                }

                //item name and lore
                val name = mm.deserialize(config.getString("$path.name") ?: "<red>Unnamed Item</red>")
                val lore = config.getStringList("$path.lore").map { mm.deserialize(it) }

                val enchantments = getEnchantments(config.getConfigurationSection("$path.enchantments"))

                val itemData = ItemData(
                    id = id,
                    material = material,
                    name = name,
                    lore = lore,
                    enchantments = enchantments,
                    commands = config.getStringList("$path.commands"),
                    permissionRequired = config.getBoolean("$path.permission-required", false),
                    cooldown = config.getLong("$path.cooldown", 0),
                    consume = config.getBoolean("$path.consume", false)
                )

                specialItems[id] = itemData
                registerUsePermission(id)
                plugin.logger.info("Loaded command item: $id")

            } catch (e: Exception) {
                plugin.logger.severe("Failed to load command item '$id'. Error: ${e.message}")
            }
        }
    }

    private fun unregisterUsePermissions() {
        plugin.server.pluginManager.permissions
            .filter { it.name.startsWith(PERM_USE) }
            .forEach {
                plugin.server.pluginManager.removePermission(it.name)
                plugin.logger.info("Unregistered permission: ${it.name}")
            }
    }

    private fun registerUsePermission(id: String) {
        val permName = "$PERM_USE.$id"
        if (plugin.server.pluginManager.getPermission(permName) == null) {
            val permission = Permission(permName, "Allows usage of the command item '$id'.")
            plugin.server.pluginManager.addPermission(permission)
        }
    }

    private fun getEnchantments(configSection : ConfigurationSection?) : Map<Enchantment, Int> {
        if (configSection == null) return mapOf()
        val enchantments = mutableMapOf<Enchantment, Int>()
        configSection.getKeys(false).forEach { enchantName ->
            val enchant = getRegistry(ENCHANTMENT, namespacedKey(enchantName))
            if (enchant != null) {
                enchantments[enchant] = configSection.getInt("$enchantName")
            } else {
                plugin.logger.warning("Invalid enchantment '$enchantName'.")
            }
        }
        return enchantments
    }

    fun getItemData(id: String): ItemData? = specialItems[id]

    fun getAllItemIds(): Set<String> = specialItems.keys

    fun createItemStack(id: String, amount: Int): ItemStack? {
        val data = getItemData(id) ?: return null
        val itemStack = ItemStack(data.material, amount)
        PDCUtils.applyDataToItemStack(itemStack, data)
        return itemStack
    }
}