package me.axiumyu.commandItem2

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

data class ItemData(
    val id: String,
    val material: Material,
    val name: Component,
    val lore: List<Component>,
    val enchantments: Map<Enchantment, Int>,
    val commands: List<String>,
    val permissionRequired: Boolean,
    val cooldown: Long, // in seconds
    val consume: Boolean
)
