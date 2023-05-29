package de.joshicodes.rja.rest.message;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Attachment;
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

    private List<Attachment> attachments;

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

    public MessageEditAction setAttachments(Attachment... attachments) {
        this.attachments = List.of(attachments);
        return this;
    }

    public MessageEditAction setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public MessageEditAction addAttachment(Attachment attachment) {
        if(attachments == null) attachments = new ArrayList<>();
        attachments.add(attachment);
        return this;
    }

    @Override
    protected Message execute() {
        EditMessageRequest request = new EditMessageRequest(message, content, embeds, attachments);
        if(!request.hasData() || (!request.hasData("content") && !request.hasData("embeds"))) {
            return message; // Nothing to edit, return original message
        }
        return getRJA().getRequestHandler().sendRequest(getRJA(), request);
    }

}
