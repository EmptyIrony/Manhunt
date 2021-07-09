package cn.charlotte.plugin.runnable

import cn.charlotte.plugin.Game
import cn.charlotte.plugin.util.chat.CC
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * Created by EmptyIrony on 2021/6/21.
 */
object GameRunnable : BukkitRunnable() {
    private val trackLocMap = HashMap<UUID, HashMap<World, Location>>()
    val tracks = HashMap<UUID, UUID>()

    override fun run() {
        loop@ for (player in Bukkit.getOnlinePlayers()) {
            //cache player location
            var map = trackLocMap[player.uniqueId]
            if (map == null) {
                map = HashMap()
                trackLocMap[player.uniqueId] = map
            }
            map[player.world] = player.location

            val uuid = tracks[player.uniqueId] ?: continue
            for (item in player.inventory) {
                if (item?.type == Material.COMPASS && item.itemMeta?.displayName?.equals(CC.translate("&4&l追踪器")) == true) {
                    continue@loop
                }
            }
            val item = ItemStack(Material.COMPASS)
            val targetMap = trackLocMap[uuid] ?: continue@loop
            val location = targetMap[player.world] ?: continue@loop
            val meta = item.itemMeta as CompassMeta
            meta.setDisplayName(CC.translate("&4&l追踪器"))
            meta.isLodestoneTracked = false
            meta.lodestone = location
            item.itemMeta = meta
            player.inventory.addItem(item)
        }
    }
}