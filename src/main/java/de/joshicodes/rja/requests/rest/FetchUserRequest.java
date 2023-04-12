package de.joshicodes.rja.requests.rest;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.User;

public class FetchUserRequest extends RestRequest<User> {

    public FetchUserRequest(String userId) {
        super("GET", "/users/" + userId);
    }

    @Override
    public User fetch(RJA rja, JsonObject data) {
        return User.from(rja, data);
    }

}
