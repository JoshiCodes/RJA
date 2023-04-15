package de.joshicodes.rja.rest.message;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.requests.rest.message.EditMessageRequest;
import de.joshicodes.rja.rest.RestAction;

import java.util.ArrayList;
import java.util.List;

public class MessageEditAction extends RestAction<Message> {

    private final Message message;

    private String content;
    private List<MessageEmbed> embeds;

    public MessageEditAction(final RJA rja, final Message message) {
        super(rja);
        this.message = message;
    }

    public MessageEditAction setContent(String content) {
        this.content = content;
        return this;
    }

    public MessageEditAction setEmbeds(MessageEmbed... embeds) {
        this.embeds = List.of(embeds);
        return this;
    }

    public MessageEditAction setEmbeds(List<MessageEmbed> embeds) {
        this.embeds = embeds;
        return this;
    }

    public MessageEditAction addEmbed(MessageEmbed embed) {
        if(embeds == null) embeds = new ArrayList<>();
        embeds.add(embed);
        return this;
    }

    @Override
    public Message complete() {
        EditMessageRequest request = new EditMessageRequest(message, content, embeds);
        if(!request.hasData() || (!request.hasData("content") && !request.hasData("embeds"))) {
            return message; // Nothing to edit, return original message
        }
        return getRJA().getRequestHandler().sendRequest(getRJA(), request);
    }

}
