package de.joshicodes.rja.object.channel;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.rest.SimpleRestAction;
import de.joshicodes.rja.util.JsonUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class TextChannel extends ServerChannel {

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

        TextChannel tc = new TextChannel() {
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
        tc.cachedHistory = new ArrayList<>();
        if(lastMessageId != null)
            tc.cachedHistory.add(lastMessageId);
        return tc;

    }

    private List<String> cachedHistory;

    abstract public String getServerId();
    abstract public String getName();
    abstract public String getDescription();

    //abstract ChannelIcon getIcon(); // TODO

    @Nullable
    public String lastMessageId() {
        if(getCachedHistory().isEmpty()) {
            return null;
        }
        if(getCachedHistory().size() > 1) {
            return getCachedHistory().get(getCachedHistory().size() - 2);
        }
        return getCachedHistory().get(0);
    }

    public List<String> getCachedHistory() {
        return cachedHistory;
    }

    abstract public boolean isNsfw();

    //abstract public Permission[] getDefaultPermissions(); // TODO
    //abstract public Permission getRolePermissions(); // TODO

    @Override
    public SimpleRestAction<Void> close() {
        // TODO
        return null;
    }

    public String getAsMention() {
        return "<#" + getId() + ">";
    }

    @Override
    public ChannelType getType() {
        return ChannelType.TEXT_CHANNEL;
    }

}
