package cn.charlotte.plugin

import cn.charlotte.plugin.`object`.Team
import cn.charlotte.plugin.extend.seaking.SeaKing
import cn.charlotte.plugin.runnable.PreGameRunnable
import cn.charlotte.plugin.util.chat.CC
import cn.charlotte.plugin.util.chat.MessageBuilder
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by EmptyIrony on 2021/6/22.
 */
class ManHunt: JavaPlugin(){
    companion object {
        lateinit var instance: ManHunt
    }

    override fun onEnable() {
        instance = this
        PreGameRunnable.runTaskTimer(this,20,20)
        SeaKing.init()

        MinecraftServer.getServer().allowFlight = true

        for (player in Bukkit.getOnlinePlayers()) {
            player.loadData()
        }

        Bukkit.getOnlinePlayers().forEach{
            it.loadData()
        }
    }

    override fun onCommand(player: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) {
            return true
        }
        if (command.name.equals("start",true)) {
            if (Game.start) {
                return true
            }
            if (PreGameRunnable.time == -1) {
                PreGameRunnable.time = 5
                Bukkit.broadcastMessage(CC.translate("&c游戏将在5秒后开始,请做好准备"))
            } else {
                PreGameRunnable.time = -1
                Bukkit.broadcastMessage(CC.translate("&c游戏开始由 &6${player.name} &c暂停"))
            }
            return true
        }
        if (command.name.equals("hunter",true)) {
            Game.addTeam(player, Team.HUNTER)
            player.sendMessage("你加入了 Hunter 阵营")
            return true
        }
        if (command.name.equals("runner",true)) {
            Game.addTeam(player, Team.RUNNER)
            player.sendMessage("你加入了 Runner 阵营")
            SeaKing.seakings.remove(player.uniqueId)
            return true
        }
        if (command.name.equals("seaking",true)) {
            val team = Game.getTeam(player)
            if (team == Team.NONE || team == Team.RUNNER) {
                player.sendMessage("你必须成为 Hunter,才可以成为海王")
            } else {
                player.sendMessage("你加入了 SeaKing 阵营")
                SeaKing.seakings.add(player.uniqueId)
            }
            return true
        }

        player.sendMessage("")
        if (!Game.start) {
            val time = PreGameRunnable.time
            if (time == -1) {
                player.spigot().sendMessage(
                    MessageBuilder()
                        .addText("&c&l[开始]")
                        .addShowText("&7点击开始游戏")
                        .addCommand("/start")
                        .build()
                )
            } else {
                player.spigot().sendMessage(
                    MessageBuilder()
                        .addText("&c&l[停止]")
                        .addShowText("&7点击暂停开始游戏")
                        .addCommand("/start")
                        .build()
                )
            }
            player.sendMessage("")
            player.sendMessage("选择加入的队伍")
            player.spigot().sendMessage(
                MessageBuilder()
                .addText("&b&l[HUNTER]   ")
                .addCommand("/hunter")
                .addExtra(
                    MessageBuilder()
                        .addText("&c&l[RUNNER]  ")
                        .addCommand("/runner")
                        .addExtra(
                            MessageBuilder()
                                .addText("&9&l[SEA_KING]")
                                .addCommand("/seaking")
                                .build()
                        )
                        .build()
                )
                .build())
            player.sendMessage("")
        }
        return true
    }
}