package de.joshicodes.rja.requests.rest.user.self;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchSelfRequest extends RestRequest<User> {

    public FetchSelfRequest() {
        super("/users/@me");
    }

    @Override
    public User fetch(RJA rja, JsonElement data) {
        if(!data.isJsonObject())
            return null;
        return User.from(rja, data.getAsJsonObject());
    }

}
