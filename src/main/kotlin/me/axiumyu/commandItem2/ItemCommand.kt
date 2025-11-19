package me.axiumyu.commandItem2

import me.axiumyu.commandItem2.CommandItem2.Companion.PERM_ADMIN
import me.axiumyu.commandItem2.CommandItem2.Companion.mm
import me.axiumyu.commandItem2.PDCUtils.addExtraInfo
import me.axiumyu.commandItem2.PDCUtils.readDataFromItemStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object ItemCommand : CommandExecutor, TabCompleter {

    private val prefix = mm.deserialize("<gold>[CommandItem] ")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission(PERM_ADMIN)) {
            sender.sendMsg("<red>You don't have permission to use this command.")
        }

        if (args.isEmpty()) {
            sendHelp(sender)
        }

        when (args[0].lowercase()) {
            "reload" -> handleReload(sender)
            "get" -> handleGet(sender, args)
            else -> sendHelp(sender)
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (!sender.hasPermission(PERM_ADMIN)) return emptyList()

        if (args.size == 1) {
            return listOf("reload", "get").filter { it.startsWith(args[0], true) }
        }

        if (args.size == 2 && args[0].equals("get", true)) {

            return ItemManager.getAllItemIds().filter { it.startsWith(args[1], true) }
        }

        return emptyList()
    }

    private fun Audience.sendMsg(msg: String, hasPrefix: Boolean = true) {
        if (hasPrefix) {
            this.sendMessage(prefix.append { mm.deserialize(msg) })
        } else {
            this.sendMessage(mm.deserialize(msg))
        }
    }

    private fun Audience.sendMsg(msg: Component, hasPrefix: Boolean = true) {
        if (hasPrefix) {
            this.sendMessage(prefix.append { msg })
        } else {
            this.sendMessage(msg)
        }
    }

    private fun handleReload(sender: CommandSender) {
        getPlugin(CommandItem2::class.java).reload()
        sender.sendMsg("<green>Configuration reloaded.")
    }

    private fun handleGet(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) {
            sender.sendMsg("<red>This command can only be used by players.")
            return
        }

        if (args.size < 2) {
            sender.sendMsg("<red>Usage: /si get <id> [amount]")
            return
        }

        val itemId = args[1] ?: ""
        val amount = args.getOrNull(2)?.toIntOrNull() ?: 1

        val itemStack = ItemManager.createItemStack(itemId, amount)
        if (itemStack == null) {
            sender.sendMsg("<red>Item with ID '$itemId' not found.")
            return
        }
        readDataFromItemStack(itemStack)?.let { addExtraInfo(itemStack, it) }

        sender.inventory.addItem(itemStack)
        sender.sendMsg(
            mm.deserialize("<green>You received ")
                .append(itemStack.displayName().hoverEvent(itemStack.asHoverEvent()))
                .append(
                    mm.deserialize("<green> (x$amount).")
                ), true
        )
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMsg("<yellow>Available Commands:")
        sender.sendMsg("<aqua> - /ci reload: <gray>Reloads the configuration.")
        sender.sendMessage("<aqua> - /ci get <id> [amount]: <gray>Gives you a special item.")
    }
}