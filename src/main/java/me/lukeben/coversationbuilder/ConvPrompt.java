package me.lukeben.coversationbuilder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.lukeben.utils.Methods;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.function.Consumer;

@Getter
@Setter
@Builder
public class ConvPrompt extends StringPrompt {

    private String promptText;
    private ConversationContext receivedContext;
    private String receivedInput;
    private Consumer<ConvPrompt> answer;
    private Prompt prompt;

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Methods.color(promptText);
    }

    @Override
    public Prompt acceptInput(ConversationContext receivedContextContext, String receivedInput) {
        this.receivedContext = receivedContextContext;
        this.receivedInput = receivedInput;
        answer.accept(this);
        return prompt != null ? prompt : END_OF_CONVERSATION;
    }

}