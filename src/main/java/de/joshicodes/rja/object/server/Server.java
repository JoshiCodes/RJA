package de.joshicodes.rja.object.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class Server {

    public static Server from(RJA rja, JsonObject object) {

        if(object == null) return null;
        if(!object.has("_id")) return null;

        final String id = object.get("_id").getAsString();
        final String ownerId = object.get("owner").getAsString();
        final String name = JsonUtil.getString(object, "name", null);
        final String description = JsonUtil.getString(object, "description", null);
        final List<String> channels = new ArrayList<>();
        for(JsonElement channel : JsonUtil.getArray(object, "channels", new JsonArray())) {
            if(!channel.isJsonPrimitive()) continue;
            String channelId = channel.getAsString();
            if(channelId == null) continue;
            channels.add(channelId);
        }


        return new Server() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getOwnerId() {
                return ownerId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public List<String> getChannelIds() {
                return channels;
            }

        };
    }

    abstract public String getId();
    abstract public String getOwnerId();
    abstract public String getName();
    abstract public String getDescription();

    abstract public List<String> getChannelIds();

}
