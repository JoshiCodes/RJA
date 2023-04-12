package de.joshicodes.rja.requests.rest;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Message;

public class FetchMessageRequest extends RestRequest<Message> {

    public FetchMessageRequest(String channel, String id) {
        super("/channels/" + channel + "/messages/" + id);
    }

    @Override
    public Message fetch(RJA rja, JsonObject data) {
        return null;
    }

}
