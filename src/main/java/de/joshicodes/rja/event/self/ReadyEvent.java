package de.joshicodes.rja.event.self;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.Event;
import de.joshicodes.rja.event.IncomingEvent;

import javax.annotation.Nullable;

public class ReadyEvent extends IncomingEvent {

    public ReadyEvent() {
        this(null);
    }

    public ReadyEvent(RJA rja) {
        super(rja, "Ready");
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {
        JsonArray users = object.get("users").getAsJsonArray();
        for(JsonElement user : users) {
            if(!user.isJsonObject()) continue;
            rja.cacheUser(user.getAsJsonObject());
        }
        JsonArray servers = object.get("servers").getAsJsonArray();
        JsonArray channels = object.get("channels").getAsJsonArray();
        JsonArray members = object.get("members").getAsJsonArray();
        return new ReadyEvent(rja);
    }

}
