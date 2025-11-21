package me.axiumyu.commandItem2

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import java.util.concurrent.TimeUnit

object PAPIExpansion : PlaceholderExpansion() {

    override fun getIdentifier(): String = "commanditem"
    override fun getAuthor(): String = "Axiumyu"
    override fun getVersion(): String = "1.0.0"
    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null) return null

        val parts = params.split("_", limit = 2)
        if (parts.size < 2) return null

        val type = parts[0]
        val itemId = parts[1]

        return when (type.lowercase()) {
            "cooldown" -> {
                val remainingMillis = ItemListener.getCooldown(player.uniqueId, itemId)
                if (remainingMillis <= 0) "Ready" else formatDuration(remainingMillis)
            }
            "cooldown_s" -> {
                val remainingMillis = ItemListener.getCooldown(player.uniqueId, itemId)
                if (remainingMillis <= 0) "0" else (remainingMillis / 1000).toString()
            }
            "name" -> {
                ItemManager.getItemData(itemId)?.name?.let {
                    // PlaceholderAPI often works best with legacy strings
                    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(it)
                } ?: "Unknown Item"
            }
            else -> null
        }
    }

    private fun formatDuration(millis: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(millis)

        return when {
            hours > 0 -> String.format("%dh %dm %ds", hours, minutes, seconds)
            minutes > 0 -> String.format("%dm %ds", minutes, seconds)
            else -> String.format("%.1fs", millis / 1000.0)
        }
    }
}