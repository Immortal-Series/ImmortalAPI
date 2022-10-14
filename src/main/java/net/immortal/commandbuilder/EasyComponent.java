package net.immortal.commandbuilder;

import net.immortal.utils.Methods;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EasyComponent {

    private final List<BaseComponent> parts = new ArrayList<>();
    private BaseComponent current;

    public EasyComponent(EasyComponent original) {
        current = original.current.duplicate();

        for (final BaseComponent baseComponent : original.parts)
            parts.add(baseComponent.duplicate());
    }

    public EasyComponent(String text) {
        current = new TextComponent(TextComponent.fromLegacyText(Methods.color(text)));
    }

    public EasyComponent(BaseComponent component) {
        current = component.duplicate();
    }

    public static final EasyComponent builder(String text) {
        return new EasyComponent(text);
    }

    public EasyComponent onClickRunCmd(String text) {
        return onClick(Action.RUN_COMMAND, text);
    }

    public EasyComponent onClickSuggestCmd(String text) {
        return onClick(Action.SUGGEST_COMMAND, text);
    }

    public EasyComponent onClick(Action action, String text) {
        current.setClickEvent(new ClickEvent(action, Methods.color(text)));
        return this;
    }

    public EasyComponent onHover(String text) {
        return onHover(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, text);
    }

    public EasyComponent onHover(net.md_5.bungee.api.chat.HoverEvent.Action action, String text) {
        current.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(Methods.color(text))));

        return this;
    }

    /*public EasyComponent retain(ComponentBuilder.FormatRetention retention) {
        current.retain(retention);
        return this;
    }*/

    public BaseComponent[] create() {
        final BaseComponent[] result = parts.toArray(new BaseComponent[parts.size() + 1]);
        result[parts.size()] = current;

        return result;
    }

    public void send(Player... players) {
        final BaseComponent[] comp = create();

        for (final Player player : players)
            player.spigot().sendMessage(comp);
    }
}
