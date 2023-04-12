package de.joshicodes.rja.requests.rest.self;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.User;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchSelfRequest extends RestRequest<User> {

    public FetchSelfRequest() {
        super("/users/@me");
    }

    @Override
    public User fetch(RJA rja, JsonObject data) {
        return User.from(rja, data);
    }

}
