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
        this(null, null, null, null);
    }

    private final TextChannel channel;
    private final User author;
    private final Message message;

    public MessageReceivedEvent(RJA rja, User author, Message message, TextChannel channel) {
        super(rja, "Message");
        this.author = author;
        this.message = message;
        this.channel = channel;
    }

    public TextChannel getChannel() {
        return channel;
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
        return getServer() != null;
    }

    @Nullable
    public Server getServer() {
        if(message == null || message.getChannel().complete() == null)
            return null;
        if(message.getChannel().complete() instanceof ServerChannel sc) {
            return sc.getServer().complete();
        }
        return null;
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
        TextChannel textChannel = null;
        if(message.getChannel().complete() instanceof TextChannel tc) {
            textChannel = tc;
        }
        if(rja.getChannelCache().containsKey(message.getChannelId())) {
            if(rja.getChannelCache().get(message.getChannelId()) instanceof TextChannel tc) {
                tc.getCachedHistory().add(message.getId());
            }
        }
        return new MessageReceivedEvent(rja, author, message, textChannel);
    }

}
