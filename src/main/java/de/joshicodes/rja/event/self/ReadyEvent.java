package de.joshicodes.rja.event.self;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.Event;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.rest.RestAction;

import javax.annotation.Nullable;

public class ReadyEvent extends IncomingEvent {

    public ReadyEvent() {
        this(null);
    }

    public ReadyEvent(RJA rja) {
        super(rja, "Ready");
    }

    public User getSelf() {
        return retrieveSelf().complete();
    }

    public RestAction<User> retrieveSelf() {
        return getRJA().retrieveSelfUser();
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {
        JsonArray users = object.get("users").getAsJsonArray();
        for(JsonElement user : users) {
            if(!user.isJsonObject()) continue;
            rja.cacheUser(user.getAsJsonObject());
        }
        JsonArray servers = object.get("servers").getAsJsonArray();
        for(JsonElement server : servers) {
            if(!server.isJsonObject()) continue;
            rja.cacheServer(server.getAsJsonObject());
        }
        JsonArray channels = object.get("channels").getAsJsonArray();
        for(JsonElement channel : channels) {
            if(!channel.isJsonObject()) continue;
            rja.cacheChannel(channel.getAsJsonObject());
        }
        JsonArray members = object.get("members").getAsJsonArray();
        // TODO: Cache members
        return new ReadyEvent(rja);
    }

}
