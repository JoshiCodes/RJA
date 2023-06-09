package de.joshicodes.rja.event.message;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.channel.ServerChannel;
import de.joshicodes.rja.object.channel.TextChannel;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.object.user.User;

import javax.annotation.Nullable;

public class MessageReceivedEvent extends IncomingEvent {

    public MessageReceivedEvent() {
        this(null, null, null);
    }

    private final User author;
    private final Message message;

    public MessageReceivedEvent(RJA rja, User author, Message message) {
        super(rja, "Message");
        this.author = author;
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public Message getMessage() {
        return message;
    }

    public boolean fromBot() {
        return author.getBotInfo().isBot();
    }

    public boolean isFromServer() {
        return message.getChannel().complete() instanceof ServerChannel;
    }

    @Nullable
    public Server getServer() {
            if(!isFromServer()) {
            return null;
        }
        return ((ServerChannel) message.getChannel().complete()).getServer().complete();
    }

    @Override
    public MessageReceivedEvent handle(RJA rja, JsonObject object) {
        if(object == null || !object.has("type")) {
            return null;
        }
        if(!object.get("type").getAsString().equals("Message")) {
            return null;
        }
        String authorId = object.get("author").getAsString();
        User author = rja.retrieveUser(authorId).complete();
        Message message = Message.from(rja, object, null);
        rja.cacheMessage(message);
        // update cached channel
        rja.getChannelCache().stream().filter(c -> c.getId().equals(message.getChannelId())).findFirst().ifPresent(c -> {
            if(c instanceof TextChannel tc) {
                TextChannel.updateLastMessageId(tc, message.getId());
            }
        });
        return new MessageReceivedEvent(rja, author, message);
    }

}
