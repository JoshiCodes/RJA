package de.joshicodes.rja.event.message;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.requests.rest.message.FetchMessageRequest;

import java.util.logging.Logger;

public class MessageUpdateEvent extends IncomingEvent {

    public MessageUpdateEvent() {
        this(null, null, null);
    }

    private final GenericChannel channel;
    private final Message message;

    public MessageUpdateEvent(RJA rja, GenericChannel channel, Message message) {
        super(rja, "MessageUpdate");
        this.channel = channel;
        this.message = message;
    }

    public GenericChannel getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {
        String id = object.get("id").getAsString();
        String channel = object.get("channel").getAsString();
        boolean inCache = rja.getMessageCache().containsIf(m -> m.equals(id));
        Message message;
        if(!inCache) {
            // Message not in cache, cannot update with partial data -> fetch full message
            message = rja.getRequestHandler().sendRequest(rja, new FetchMessageRequest(channel, id));
        } else message = rja.getMessageCache().getIf(m -> m.equals(id));
        Message updated = Message.from(rja, object.get("data").getAsJsonObject(), message);
        rja.cacheMessage(updated);
        return new MessageUpdateEvent(rja, rja.getChannelCache().getIf(c -> c.getId().equals(channel)), updated);
    }

    @Override
    public RJA getRJA() {
        return super.getRJA();
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

}
