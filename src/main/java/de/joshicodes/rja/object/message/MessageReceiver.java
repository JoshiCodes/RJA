package de.joshicodes.rja.object.message;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Attachment;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.rest.message.MessageSendAction;

import java.util.List;

public abstract class MessageReceiver {

    abstract public RJA getRJA();
    abstract public String getId();

    public MessageSendAction sendMessage(String content) {
        return new MessageSendAction(getRJA(), getId()).setContent(content);
    }

    public MessageSendAction sendEmbeds(MessageEmbed embed) {
        return new MessageSendAction(getRJA(), getId()).setEmbeds(embed);
    }

    public MessageSendAction sendEmbeds(MessageEmbed... embeds) {
        return new MessageSendAction(getRJA(), getId()).setEmbeds(embeds);
    }

    public MessageSendAction sendFile(Attachment attachment) {
        return new MessageSendAction(getRJA(), getId()).setAttachments(List.of(attachment));
    }


}
