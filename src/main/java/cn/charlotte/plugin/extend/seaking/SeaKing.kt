package cn.charlotte.plugin.extend.seaking

import cn.charlotte.plugin.Game
import cn.charlotte.plugin.ManHunt
import cn.charlotte.plugin.events.GameStartEvent
import cn.charlotte.plugin.util.chat.CC
import com.google.common.io.ByteStreams
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPosition
import net.minecraft.network.PacketDataSerializer
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload
import net.minecraft.resources.MinecraftKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * Created by EmptyIrony on 2021/6/22.
 */
object SeaKing : Listener {
    val seakings = ArrayList<UUID>()
    lateinit var item: ItemStack


    fun init() {
        Bukkit.getPluginManager().registerEvents(SeaKing, ManHunt.instance)

        item = ItemStack(Material.TRIDENT)
        item.addUnsafeEnchantment(Enchantment.RIPTIDE, 3)
        item.addUnsafeEnchantment(Enchantment.IMPALING, 5)
        val meta = item.itemMeta!!
        meta.isUnbreakable = true
        meta.setDisplayName(CC.translate("&9&l海王"))
        item.itemMeta = meta

        Bukkit.getServer().messenger.registerOutgoingPluginChannel(ManHunt.instance, "charlotte:sea_king")
        object : BukkitRunnable() {
            override fun run() {
                if (!Game.start) {
                    return
                }
                //make runner knows where is sea king at
                boardCastSeaKingLocation()

                seakings.forEach {
                    val temp = Bukkit.getPlayer(it)
                    if (temp?.world?.environment == World.Environment.NORMAL) {
                        boardCastJoinOverWorld(it)
                    } else {
                        boardCastQuitOverWorld(it)
                    }

                    val player = Bukkit.getPlayer(it) ?: return@forEach
                    player.addPotionEffect(PotionEffect(PotionEffectType.CONDUIT_POWER,20 * 60 * 60 * 3,0,true,false,false))
                    for (itemStack in player.inventory) {
                        if (itemStack?.type == Material.TRIDENT && itemStack.itemMeta?.displayName?.equals(
                                CC.translate(
                                    "&9&l海王"
                                ), true
                            ) == true
                        ) {
                            return@forEach
                        }
                    }
                    player.inventory.addItem(item)
                }
            }
        }.runTaskTimer(ManHunt.instance, 20, 20)

    }

    fun boardCastSeaKingLocation() {
        for (seaking in seakings) {
            val player = Bukkit.getPlayer(seaking) ?: continue
            val out = ByteStreams.newDataOutput()
            out.writeLong(player.uniqueId.mostSignificantBits)
            out.writeLong(player.uniqueId.leastSignificantBits)
            out.writeLong(BlockPosition(player.location.x, player.location.y, player.location.z).asLong())
            out.writeLong(if (player.location.world?.environment == World.Environment.NORMAL) 0L else 1L)

            for (target in Bukkit.getOnlinePlayers()) {
                val connection = (target as CraftPlayer).handle.b
                connection.sendPacket(
                    PacketPlayOutCustomPayload(
                        MinecraftKey("charlotte:location"),
                        PacketDataSerializer(Unpooled.wrappedBuffer(out.toByteArray()))
                    )
                )
            }
        }
    }

    fun boardCastQuitOverWorld(uuid: UUID) {
        val out = ByteStreams.newDataOutput()
        out.writeLong(uuid.mostSignificantBits)
        out.writeLong(uuid.leastSignificantBits)

        for (target in Bukkit.getOnlinePlayers()) {
            val connection = (target as CraftPlayer).handle.b
            connection.sendPacket(
                PacketPlayOutCustomPayload(
                    MinecraftKey("charlotte:quit_over_world"),
                    PacketDataSerializer(Unpooled.wrappedBuffer(out.toByteArray()))
                )
            )
        }
    }

    fun boardCastJoinOverWorld(uuid: UUID) {
        val out = ByteStreams.newDataOutput()
        out.writeLong(uuid.mostSignificantBits)
        out.writeLong(uuid.leastSignificantBits)

        for (target in Bukkit.getOnlinePlayers()) {
            val connection = (target as CraftPlayer).handle.b
            connection.sendPacket(
                PacketPlayOutCustomPayload(
                    MinecraftKey("charlotte:join_over_world"),
                    PacketDataSerializer(Unpooled.wrappedBuffer(out.toByteArray()))
                )
            )
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val to = event.to ?: return
        val from = event.from

        if (to.world != from.world) {
            if (to.world?.environment != World.Environment.NORMAL) {
                boardCastQuitOverWorld(event.player.uniqueId)
            }
            if (to.world?.environment == World.Environment.NORMAL) {
                boardCastJoinOverWorld(event.player.uniqueId)
            }
        }
    }

    fun sendSetSeaKingPacketToPlayer(uuid: UUID, player: Player) {
        val out = ByteStreams.newDataOutput()
        out.writeBoolean(true)
        out.writeLong(uuid.mostSignificantBits)
        out.writeLong(uuid.leastSignificantBits)

        val connection = (player as CraftPlayer).handle.b
        connection.sendPacket(
            PacketPlayOutCustomPayload(
                MinecraftKey("charlotte:sea_king"),
                PacketDataSerializer(Unpooled.wrappedBuffer(out.toByteArray()))
            )
        )
    }

    fun sendSetSeaKingPacket(uuid: UUID) {
        for (player in Bukkit.getOnlinePlayers()) {
            this.sendSetSeaKingPacketToPlayer(uuid, player)
        }
    }

    fun sendRemoveAllSeaKingPacket() {
        for (player in Bukkit.getOnlinePlayers()) {
            sendRemoveAllSeaKingPacketToPlayer(player)
        }
    }

    fun sendRemoveAllSeaKingPacketToPlayer(player: Player) {
        val out = ByteStreams.newDataOutput()
        out.writeBoolean(false)

        player.sendPluginMessage(ManHunt.instance, "charlotte:sea_king", out.toByteArray())
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (Game.start) {
            this.sendRemoveAllSeaKingPacketToPlayer(event.player)
            for (seaking in this.seakings) {
                this.sendSetSeaKingPacketToPlayer(seaking, event.player)
            }
        }
    }

    @EventHandler
    fun onStart(event: GameStartEvent) {
        if (this.seakings.isEmpty()) {
            return
        }
        val boots = ItemStack(Material.NETHERITE_BOOTS)
        boots.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER,3)
        val itemMeta = boots.itemMeta!!
        itemMeta.setDisplayName(CC.translate("&9&l海王"))
        itemMeta.isUnbreakable = true
        boots.itemMeta = itemMeta

        val helmet = ItemStack(Material.NETHERITE_HELMET)
        helmet.addUnsafeEnchantment(Enchantment.WATER_WORKER,3)
        val itemMeta2 = helmet.itemMeta!!
        itemMeta2.setDisplayName(CC.translate("&9&l海王"))
        itemMeta2.isUnbreakable = true
        helmet.itemMeta = itemMeta2

        for (seaking in this.seakings) {
            this.sendSetSeaKingPacket(seaking)
            this.boardCastJoinOverWorld(seaking)

            val player = Bukkit.getPlayer(seaking) ?: continue
            player.inventory.addItem(boots)
            player.inventory.addItem(helmet)
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (!event.keepInventory) {
            event.drops.removeIf {
                it?.itemMeta?.displayName?.equals(CC.translate("&9&l海王"), true) == true
            }
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (Game.start && seakings.contains(event.player.uniqueId)) {
            val it = event.itemDrop.itemStack
            if (it.type == Material.TRIDENT && it.itemMeta?.displayName?.equals(CC.translate("&9&l海王"), true) == true) {
                event.isCancelled = true
            }
        }
    }
}