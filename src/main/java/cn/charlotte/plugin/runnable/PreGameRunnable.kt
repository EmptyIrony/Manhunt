package cn.charlotte.plugin.runnable

import cn.charlotte.plugin.Game
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * Created by EmptyIrony on 2021/6/22.
 */
object PreGameRunnable: BukkitRunnable(){


    var time = -1

    override fun run() {
        if (time == -1) {
            return
        }

        time--
        if (time == 0) {
            Game.startGame()
        }
    }
}