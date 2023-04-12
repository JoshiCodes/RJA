package de.joshicodes.rja.event.message;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.Message;
import de.joshicodes.rja.object.User;

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

    @Override
    public MessageReceivedEvent handle(RJA rja, JsonObject object) {
        if(object == null || !object.has("type")) {
            return null;
        }
        if(!object.get("type").getAsString().equals("Message")) {
            return null;
        }
        final String messageId = object.get("_id").getAsString();
        final String nonce = object.get("nonce").getAsString();
        final String authorId = object.get("author").getAsString();
        final User author = rja.retrieveUser(authorId).complete();
        final String channelId = object.get("channel").getAsString();
        final String content = object.get("content").getAsString();
        Message message = new Message() {
            @Override
            public String getId() {
                return messageId;
            }

            @Override
            public String getNonce() {
                return nonce;
            }

            @Override
            public String getChannel() {
                return channelId;
            }

            @Override
            public User getAuthor() {
                return author;
            }

            @Override
            public String getContent() {
                return content;
            }
        };
        rja.cacheMessage(message);
        return new MessageReceivedEvent(rja, author, message);
    }

}
