package de.joshicodes.rja.requests.rest.message;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchMessageRequest extends RestRequest<Message> {

    public FetchMessageRequest(String channel, String id) {
        super("/channels/" + channel + "/messages/" + id);
    }

    @Override
    public Message fetch(RJA rja, int responseCode, JsonElement data) {
        if (!data.isJsonObject())
            return null;
        return Message.from(rja, data.getAsJsonObject(), null);
    }

}
