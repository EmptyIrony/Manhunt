package cn.charlotte.plugin.util.chat

import net.md_5.bungee.api.chat.*

/**
 * Created by EmptyIrony on 2021/6/22.
 */
class MessageBuilder {
    private val component: TextComponent = TextComponent()

    fun addText(text: String): MessageBuilder {
        component.addExtra(CC.translate(text))
        return this
    }

    fun addShowText(text: String): MessageBuilder {
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,ComponentBuilder(CC.translate(text)).create())
        return this
    }

    fun addCommand(command: String): MessageBuilder {
        component.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND,command)
        return this
    }

    fun addExtra(component: BaseComponent): MessageBuilder {
        this.component.addExtra(component)
        return this
    }

    fun build(): TextComponent{
        return component
    }

}