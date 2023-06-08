package de.joshicodes.rja.requests.rest.interaction;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.requests.rest.RestRequest;

public class RemoveReactionRequest extends RestRequest<Boolean> {

    public RemoveReactionRequest(String channel, String message, String emoji, String userId, boolean removeAll) {
        super("DELETE", "/channels/" + channel + "/messages/" + message + "/reactions/" + emoji);

        if(userId != null) addData("user_id", userId);
        if(removeAll) addData("remove_all", true);  // Not needed if false

    }

    @Override
    public Boolean fetch(RJA rja, int responseCode, JsonElement data) {
        return responseCode == 204;
    }

}
