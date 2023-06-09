package de.joshicodes.rja.object;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.util.JsonUtil;

public abstract class Emoji implements IMentionable {

    public static Emoji from(RJA rja, JsonObject data) {
        if(data == null || data.isJsonNull()) return null;
        if(data.has("type")) {
            if(data.get("type").getAsString().equals("NotFound")) return null;
        }

        final String id = data.get("_id").getAsString();
        final JsonObject parent = data.get("parent").getAsJsonObject();
        if(parent == null || parent.isJsonNull()) return null;

        final String parentType = parent.get("type").getAsString();
        final boolean isServer = parentType.equals("Server");

        final String parentId = isServer ? parent.get("id").getAsString() : null;

        final String creatorId = data.get("creator_id").getAsString();
        final String name = data.get("name").getAsString();
        final boolean animated = JsonUtil.getBoolean(data, "animated", false);
        final boolean nsfw = JsonUtil.getBoolean(data, "nsfw", false);

        return new Emoji() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public Server getParent() {
                return isServer ? rja.retrieveServer(parentId).complete() : null;
            }

            @Override
            public boolean isServerEmoji() {
                return isServer;
            }

            @Override
            public String getCreatorId() {
                return creatorId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isAnimated() {
                return animated;
            }

            @Override
            public boolean isNSFW() {
                return nsfw;
            }
        };

    }

    abstract public String getId();

    abstract public Server getParent();

    abstract public boolean isServerEmoji();

    public boolean isDetached() {
        return !isServerEmoji();
    }

    abstract public String getCreatorId();

    abstract public String getName();

    abstract public boolean isAnimated();
    abstract public boolean isNSFW();

    @Override
    public String getAsMention() {
        return ":" + getId() + ":";
    }

}
