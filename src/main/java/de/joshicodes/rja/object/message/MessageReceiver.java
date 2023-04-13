package de.joshicodes.rja.object.message;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.rest.MessageSendAction;
import de.joshicodes.rja.rest.RestAction;

public abstract class MessageReceiver {

    abstract public RJA getRJA();
    abstract public String getId();

    public MessageSendAction sendMessage(String content) {
        return new MessageSendAction(getRJA(), getId()).setContent(content);
    }

    public MessageSendAction sendEmbeds(MessageEmbed embed) {
        return new MessageSendAction(getRJA(), getId()).setEmbeds(embed);
    }

}
