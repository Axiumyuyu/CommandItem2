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

        var isStrict = false

        @JvmField
        val mm = MiniMessage.miniMessage()

        @JvmStatic
        fun <T : Keyed> getRegistry(category: RegistryKey<T>, namespacedKey: NamespacedKey) =
            registryAccess().getRegistry(category).get(namespacedKey)

        @JvmStatic
        fun namespacedKey(fullName: String): NamespacedKey {
            val list = fullName.split(":")
            if (list.size == 1) return NamespacedKey.minecraft(list[0])
            return NamespacedKey(list[0], list[1])
        }

        @JvmStatic
        fun setPAPI(player : Player,origin :String) :String {
            return PlaceholderAPI.setPlaceholders(player, origin)
        }
    }


    lateinit var itemManager: ItemManager
        private set
    lateinit var itemListener: ItemListener
        private set

    override fun onEnable() {
        // Initialize components
        PDCUtils.initialize(this)
        itemManager = ItemManager(this)
        itemListener = ItemListener(this)

        // Load configuration and register items
        saveDefaultConfig()
        itemManager.loadItems()

        // Register command and listener
        getCommand("commanditem")?.let {
            val commandExecutor = ItemCommand(this)
            it.setExecutor(commandExecutor)
            it.tabCompleter = commandExecutor
        }
        server.pluginManager.registerEvents(itemListener, this)

        // Register PlaceholderAPI expansion
//        if (server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            PAPIExpansion(this).register()
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
        itemManager.loadItems()
        logger.info("Configuration and items have been reloaded.")
    }
}

