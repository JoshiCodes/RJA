package de.joshicodes.rja.requests.rest.message;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.channel.TextChannel;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.requests.rest.RestRequest;

public class MessageSendRequest extends RestRequest<Message> {

    public MessageSendRequest(String channel) {
        super("POST", "/channels/" + channel + "/messages");
    }

    @Override
    public Message fetch(RJA rja, int responseCode, JsonElement data) {
        if(!data.isJsonObject())
            return null;
        Message m = Message.from(rja, data.getAsJsonObject(), null);
        rja.cacheMessage(m);
        if(rja.getChannelCache().containsKey(m.getChannelId())) {
            if(rja.getChannelCache().get(m.getChannelId()) instanceof TextChannel tc) {
                tc.getCachedHistory().add(m.getId());
            }
        }
        return m;
    }

}
