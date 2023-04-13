package de.joshicodes.rja.object.channel;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

import javax.annotation.Nullable;

public abstract class TextChannel extends GenericChannel {

    public static TextChannel from(final RJA rja, final JsonObject object) {
        final String type = JsonUtil.getString(object, "channel_type", null);
        if(type == null || !type.equalsIgnoreCase("TextChannel")) {
            return null;
        }
        final String id = JsonUtil.getString(object, "_id", null);
        if(id == null) {
            return null;
        }
        final String name = JsonUtil.getString(object, "name", null);
        final String serverId = JsonUtil.getString(object, "server", null);
        final String description = JsonUtil.getString(object, "description", null);
        // TODO: Icon
        final String lastMessageId = JsonUtil.getString(object, "last_message_id", null);
        // TODO: Default Permissions and Role Permissions
        final boolean nsfw = JsonUtil.getBoolean(object, "nsfw", false);

        return new TextChannel() {
            @Override
            public String getServerId() {
                return serverId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Nullable
            @Override
            public String lastMessageId() {
                return lastMessageId;
            }

            @Override
            public boolean isNsfw() {
                return nsfw;
            }

            @Override
            public RJA getRJA() {
                return rja;
            }

            @Override
            public String getId() {
                return id;
            }
        };

    }

    abstract public String getServerId();
    abstract public String getName();
    abstract public String getDescription();

    //abstract ChannelIcon getIcon(); // TODO

    @Nullable
    abstract public String lastMessageId();

    abstract public boolean isNsfw();

    //abstract public Permission[] getDefaultPermissions(); // TODO
    //abstract public Permission getRolePermissions(); // TODO

    @Override
    public RestAction<Void> close() {
        // TODO
        return null;
    }

}