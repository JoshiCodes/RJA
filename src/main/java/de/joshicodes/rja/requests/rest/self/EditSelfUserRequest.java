package de.joshicodes.rja.requests.rest.self;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.User;
import de.joshicodes.rja.object.UserStatus;
import de.joshicodes.rja.requests.rest.RestRequest;

public class EditSelfUserRequest extends RestRequest<User> {

    public EditSelfUserRequest(RJA rja) {
        super("PATCH", "/users/@me");
    }

    public EditSelfUserRequest setBadges(int badges) {
        addData("badges", badges);
        return this;
    }

    public EditSelfUserRequest remove(JsonArray remove) {
        addData("remove", remove);
        return this;
    }

    public EditSelfUserRequest setStatus(UserStatus status) {
        JsonObject object = new JsonObject();
        if(status.text() != null) object.addProperty("text", status.text());
        if(status.presence() != null) object.addProperty("presence", status.presence().getFriendlyName());
        addData("status", object);
        return this;
    }

    @Override
    public User fetch(RJA rja, JsonObject data) {
        return User.from(rja, data);
    }

}
