package me.axiumyu.commandItem2

import io.papermc.paper.registry.RegistryKey.ENCHANTMENT
import me.axiumyu.commandItem2.CommandItem2.Companion.getRegistry
import me.axiumyu.commandItem2.CommandItem2.Companion.isStrict
import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import me.axiumyu.commandItem2.CommandItem2.Companion.namespacedKey
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.Permission

class ItemManager(private val plugin: CommandItem2) {

    private val specialItems = mutableMapOf<String, ItemData>()


    fun loadItems() {
        specialItems.clear()
        unregisterUsePermissions()

        isStrict = plugin.config.getBoolean("strict", false)
        val itemsSection = plugin.config.getConfigurationSection("items") ?: return

        for (id in itemsSection.getKeys(false)) {
            val path = "items.$id"
            try {
                val materialName = plugin.config.getString("$path.material")?.uppercase()
                val material = materialName?.let { Material.getMaterial(it) }
                if (material == null) {
                    plugin.logger.warning("Invalid material '$materialName' for item '$id'. Skipping.")
                    continue
                }

                val name = mm.deserialize(plugin.config.getString("$path.name") ?: "<red>Unnamed Item</red>")
                val lore = plugin.config.getStringList("$path.lore").map { mm.deserialize(it) }

                val enchantments = mutableMapOf<Enchantment, Int>()
                plugin.config.getConfigurationSection("$path.enchantments")?.getKeys(false)?.forEach { enchantName ->
                    val enchant = getRegistry(ENCHANTMENT, namespacedKey(enchantName))
                    if (enchant != null) {
                        enchantments[enchant] = plugin.config.getInt("$path.enchantments.$enchantName")
                    } else {
                        plugin.logger.warning("Invalid enchantment '$enchantName' for item '$id'.")
                    }

                }

                val itemData = ItemData(
                    id = id,
                    material = material,
                    name = name,
                    lore = lore,
                    enchantments = enchantments,
                    commands = plugin.config.getStringList("$path.commands"),
                    permissionRequired = plugin.config.getBoolean("$path.permission-required", false),
                    cooldown = plugin.config.getLong("$path.cooldown", 0),
                    consume = plugin.config.getBoolean("$path.consume", false)
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
            .filter { it.name.startsWith("specialitem.use.") }
            .forEach {
                plugin.server.pluginManager.removePermission(it.name)
                plugin.logger.info("Unregistered permission: ${it.name}")
            }
    }

    private fun registerUsePermission(id: String) {
        val permName = "specialitem.use.$id"
        if (plugin.server.pluginManager.getPermission(permName) == null) {
            val permission = Permission(permName, "Allows usage of the special item '$id'.")
            plugin.server.pluginManager.addPermission(permission)
        }
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