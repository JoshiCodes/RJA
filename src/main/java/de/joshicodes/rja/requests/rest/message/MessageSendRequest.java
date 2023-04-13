package de.joshicodes.rja.requests.rest.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.requests.rest.RestRequest;

public class MessageSendRequest extends RestRequest<Message> {

    public MessageSendRequest(String channel) {
        super("POST", "/channels/" + channel + "/messages");
    }

    @Override
    public Message fetch(RJA rja, JsonElement data) {
        if(!data.isJsonObject())
            return null;
        return Message.from(rja, data.getAsJsonObject());
    }

}
