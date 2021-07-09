package cn.charlotte.plugin.events

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Created by EmptyIrony on 2021/6/22.
 */
abstract class AbstractEvent: Event() {
    companion object{
        @JvmStatic
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    open fun callEvent() {
        Bukkit.getPluginManager()
            .callEvent(this)
    }
}