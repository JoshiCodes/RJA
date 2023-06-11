package de.joshicodes.rja.object.channel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.rest.SimpleRestAction;
import de.joshicodes.rja.util.JsonUtil;

public abstract class GroupChannel extends GenericChannel {

    public static GroupChannel from(final RJA rja, final JsonObject object) {

        final String type = JsonUtil.getString(object, "channel_type", null);
        if (type == null || !type.equalsIgnoreCase("Group")) {
            return null;
        }

        final String id = JsonUtil.getString(object, "_id", null);
        final String name = JsonUtil.getString(object, "name", null);
        final String ownerId = JsonUtil.getString(object, "owner", null);
        final String description = JsonUtil.getString(object, "description", null);
        final JsonArray recipients = JsonUtil.getArray(object, "recipients", null);
        String[] recipientIds = new String[recipients.size()];
        for (int i = 0; i < recipients.size(); i++) {
            recipientIds[i] = recipients.get(i).getAsString();
        }
        final String lastMessageId = JsonUtil.getString(object, "last_message_id", null);
        final boolean nsfw = JsonUtil.getBoolean(object, "nsfw", false);

        return new GroupChannel() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getOwnerId() {
                return ownerId;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String[] getRecipientIds() {
                return recipientIds;
            }

            @Override
            public String getLastMessageId() {
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

    abstract public String getName();
    abstract public String getOwnerId();
    abstract public String getDescription();
    //abstract public ChannelIcon getIcon(); // TODO
    abstract public String[] getRecipientIds();
    abstract public String getLastMessageId();
    //abstract public Permission getPermissions(); // TODO
    abstract public boolean isNsfw();

    @Override
    public SimpleRestAction<Void> close() {
        return null;
    }

    @Override
    public ChannelType getType() {
        return ChannelType.GROUP_CHANNEL;
    }

}
