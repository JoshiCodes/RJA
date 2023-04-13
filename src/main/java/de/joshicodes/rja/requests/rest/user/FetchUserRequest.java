package de.joshicodes.rja.requests.rest.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchUserRequest extends RestRequest<User> {

    public FetchUserRequest(String userId) {
        super("GET", "/users/" + userId);
    }

    @Override
    public User fetch(RJA rja, JsonElement data) {
        if(!data.isJsonObject())
            return null;
        return User.from(rja, data.getAsJsonObject());
    }

}
