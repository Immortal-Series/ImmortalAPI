package net.immortalapi.coversationbuilder;

import net.immortalapi.ImmortalAPI;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ConversationAPI {

    /**
     * @param player
     * @param prompt
     * @param timeout
     * @param quitSequence
     * @param event
     */

    public static void build(Player player, Prompt prompt, int timeout, String quitSequence, Consumer<ConversationAbandonedEvent> event) {
        final Conversation conversation = new ConversationFactory(ImmortalAPI.getInstance().getPlugin())
                .withModality(true)
                .withEscapeSequence(quitSequence)
                .addConversationAbandonedListener(e -> event.accept(e))
                .withTimeout(timeout)
                .withFirstPrompt(prompt)
                .withLocalEcho(false)
                .buildConversation(player);
        player.beginConversation(conversation);
    }

    /**
     * @param player
     * @param prompt
     * @param timeout
     * @param quitSequence
     */
    public static void build(Player player, Prompt prompt, int timeout, String quitSequence) {
        final Conversation conversation = new ConversationFactory(ImmortalAPI.getInstance().getPlugin())
                .withModality(true)
                .withEscapeSequence(quitSequence)
                .withTimeout(timeout)
                .withFirstPrompt(prompt)
                .withLocalEcho(false)
                .buildConversation(player);
        player.beginConversation(conversation);
    }

}
