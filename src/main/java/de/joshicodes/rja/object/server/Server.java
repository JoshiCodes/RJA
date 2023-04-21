package de.joshicodes.rja.object.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class Server {

    public static Server from(final RJA rja, final JsonObject object) {

        if(object == null) return null;
        if(!object.has("_id")) return null;

        final String id = object.get("_id").getAsString();
        final String ownerId = JsonUtil.getString(object, "owner", null);
        final String name = JsonUtil.getString(object, "name", null);
        final String description = JsonUtil.getString(object, "description", null);
        final List<String> channels = new ArrayList<>();
        for(JsonElement channel : JsonUtil.getArray(object, "channels", new JsonArray())) {
            if(!channel.isJsonPrimitive()) continue;
            String channelId = channel.getAsString();
            if(channelId == null) continue;
            channels.add(channelId);
        }

        Server server = new Server(rja, id, ownerId);
        server.name = name;
        server.description = description;
        server.channels = channels;
        return server;

    }

    private final RJA rja;
    private final String id;
    private final String ownerId;

    private String name;
    private String description;
    private List<String> channels;

    Server(final RJA rja, final String id, final String owner) {
        this.rja = rja;
        this.id = id;
        this.ownerId = owner;
    }

    public RJA getRJA() {
        return rja;
    }

    public String getId() {
        return id;
    }
    public String getOwnerId() {
        return ownerId;
    }

    public RestAction<User> retrieveOwner() {
        return rja.retrieveUser(ownerId);
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public List<String> getChannelIds() {
        return channels;
    }

    public void update(JsonObject server) {
        if(server == null) return;

        if(server.has("name")) {
            this.name = server.get("name").getAsString();
        }
        if(server.has("description")) {
            this.description = server.get("description").getAsString();
        }
        if(server.has("channels")) {
            this.channels.clear();
            for(JsonElement channel : server.get("channels").getAsJsonArray()) {
                if(!channel.isJsonPrimitive()) continue;
                String channelId = channel.getAsString();
                if(channelId == null) continue;
                this.channels.add(channelId);
            }
        }

        if(server.has("clear")) {
            JsonArray clear = server.get("clear").getAsJsonArray();
            for (JsonElement element : clear) {
                if(!element.isJsonPrimitive()) continue;
                String key = element.getAsString();
                if(key == null) continue;
                switch (key) {
                    case "Icon", "Banner" -> {
                        return;  // TODO (Split "Icon" and "Banner")
                    }
                    case "Description" -> {
                        this.description = null;
                    }
                }
            }
        }

    }

}
