package de.joshicodes.rja.requests.rest.interaction;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.requests.rest.RestRequest;

public class AddReactionRequest extends RestRequest<Boolean> {

    public AddReactionRequest(String channel, String message, String emoji) {
        super("PUT", "/channels/" + channel + "/messages/" + message + "/reactions/" + emoji);
    }

    @Override
    public Boolean fetch(RJA rja, int responseCode, JsonElement data) {
        return responseCode == 204;
    }

}
