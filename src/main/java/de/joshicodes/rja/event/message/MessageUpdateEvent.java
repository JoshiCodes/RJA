package de.joshicodes.rja.event.message;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.channel.ServerChannel;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.requests.rest.RestResponse;
import de.joshicodes.rja.requests.rest.message.FetchMessageRequest;

import javax.annotation.Nullable;
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

    public boolean isFromServer() {
        return channel instanceof ServerChannel;
    }

    @Nullable
    public Server getServer() {
        if(!isFromServer()) {
            return null;
        }
        return ((ServerChannel) message.getChannel().complete()).getServer().complete();
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {
        String id = object.get("id").getAsString();
        String channel = object.get("channel").getAsString();
        boolean inCache = rja.getMessageCache().containsIf(m -> m.equals(id));
        Message message;
        if(!inCache) {
            // Message not in cache, cannot update with partial data -> fetch full message
            RestResponse<Message> response = rja.getRequestHandler().fetchRequest(rja, new FetchMessageRequest(channel, id));
            if(response.isOk()) {
                message = response.object();
            } else return null;
        } else message = rja.getMessageCache().getIf(m -> m.equals(id));
        Message updated = Message.from(rja, object.get("data").getAsJsonObject(), message);
        rja.cacheMessage(updated);
        return new MessageUpdateEvent(rja, rja.getChannelCache().get(channel), updated);
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
