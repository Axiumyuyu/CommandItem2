package me.axiumyu.commandItem2

import io.papermc.paper.registry.RegistryAccess.registryAccess
import io.papermc.paper.registry.RegistryKey
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class CommandItem2 : JavaPlugin(), Listener {
    companion object {

        const val PERM_ADMIN= "commanditem.admin"

        const val PERM_USE = "commanditem.use"

        @JvmField
        var isStrict = false

        @JvmField
        val mm = MiniMessage.miniMessage()

        @JvmStatic
        fun <T : Keyed> getRegistry(category: RegistryKey<T>, namespacedKey: NamespacedKey) =
            registryAccess().getRegistry(category).get(namespacedKey)

        @JvmStatic
        fun namespacedKey(fullName: String): NamespacedKey {
            val list = fullName.split(":").map { it.lowercase() }
            if (list.size == 1) return NamespacedKey.minecraft(list[0])
            return NamespacedKey(list[0], list[1])
        }

        @JvmStatic
        fun setPAPI(player : Player,origin :String) :String {
            return PlaceholderAPI.setPlaceholders(player, origin)
        }
    }

    override fun onEnable() {
        // Load configuration and register items
        saveDefaultConfig()
        ItemManager.loadItems()

        // Register command and listener
        getCommand("commanditem")?.let {
            it.setExecutor(ItemCommand)
            it.tabCompleter = ItemCommand
        }
        server.pluginManager.registerEvents(ItemListener, this)

        // Register PlaceholderAPI expansion
//        if (server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            PAPIExpansion.register()
            logger.info("Successfully registered PlaceholderAPI expansion.")
//        } else {
//            logger.warning("PlaceholderAPI not found, placeholders will not work.")
//        }

        logger.info("CommandItem2 plugin has been enabled!")
    }

    override fun onDisable() {
        logger.info("CommandItem2 plugin has been disabled.")
    }

    fun reload() {
        reloadConfig()
        ItemManager.loadItems()
        logger.info("Configuration and items have been reloaded.")
    }
}

