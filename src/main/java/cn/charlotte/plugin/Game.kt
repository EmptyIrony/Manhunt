package cn.charlotte.plugin

import cn.charlotte.plugin.`object`.Team
import cn.charlotte.plugin.events.GameStartEvent
import cn.charlotte.plugin.runnable.GameRunnable
import cn.charlotte.plugin.runnable.PreGameRunnable
import cn.charlotte.plugin.util.chat.CC
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by EmptyIrony on 2021/6/21.
 */
object Game :Listener{
    private val runners = ArrayList<UUID>()
    private val hunters = ArrayList<UUID>()

    var start = false

    fun addTeam(player: Player,team: Team) {
        if (team == Team.HUNTER) {
            runners.remove(player.uniqueId)
            hunters.add(player.uniqueId)
        }
        if (team == Team.RUNNER) {
            hunters.remove(player.uniqueId)
            runners.add(player.uniqueId)
        }
    }

    fun getTeam(player: Player): Team {
        if (runners.contains(player.uniqueId)) {
            return Team.RUNNER
        }
        if (hunters.contains(player.uniqueId)) {
            return Team.HUNTER
        }
        return Team.NONE
    }

    fun getNextRunners(uuid: UUID): UUID{
        var cout = 0
        for (runner in runners) {
            if (runner == uuid) {
                break
            }
            cout++
        }
        cout++
        if (runners.size >= cout) {
            return runners[0]
        }
        return runners[cout]
    }

    fun startGame() {
        Bukkit.getPluginManager().registerEvents(this,ManHunt.instance)
        if (runners.size == 0){
            PreGameRunnable.time = 10
            Bukkit.broadcastMessage("Runner 队伍没有玩家,游戏开始推迟十秒")
            return
        }
        if (hunters.size == 0){
            Bukkit.broadcastMessage("Hunter 队伍没有玩家,游戏开始推迟十秒")
            PreGameRunnable.time = 10
            return
        }

        start = true
        PreGameRunnable.cancel()
        GameRunnable.runTaskTimer(ManHunt.instance,1,1)
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_ENDER_DRAGON_GROWL,1.0F,1.0F)
            it.sendMessage("游戏开始!")
        }

        for (uuid in hunters) {
            val player = Bukkit.getPlayer(uuid) ?: continue

            GameRunnable.tracks[player.uniqueId] = runners[0]
        }
        GameStartEvent.callEvent()
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.itemMeta?.displayName?.equals(CC.translate("&4&l追踪器")) == true) {
            event.itemDrop.remove()
        }
    }



}