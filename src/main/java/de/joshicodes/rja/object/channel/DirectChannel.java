package de.joshicodes.rja.object.channel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

import javax.annotation.Nullable;

public abstract class DirectChannel extends GenericChannel {

    public static DirectChannel from(final RJA rja, final JsonObject object) {

        final String type = JsonUtil.getString(object, "channel_type", null);
        if(type == null || !type.equalsIgnoreCase("DirectMessage")) {
            return null;
        }
        final String id = JsonUtil.getString(object, "_id", null);
        final boolean active = JsonUtil.getBoolean(object, "active", false);
        final JsonArray recipients = JsonUtil.getArray(object, "recipients", null);
        String[] recipientIds = new String[recipients.size()];
        for(int i = 0; i < recipients.size(); i++) {
            recipientIds[i] = recipients.get(i).getAsString();
        }
        final String lastMessageId = JsonUtil.getString(object, "last_message_id", null);

        return new DirectChannel() {
            @Override
            public RJA getRJA() {
                return rja;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public boolean isActive() {
                return active;
            }

            @Override
            public String[] getRecipients() {
                return recipientIds;
            }

            @Nullable
            @Override
            public String getLastMessageId() {
                return lastMessageId;
            }
        };
    }

    abstract public boolean isActive();
    abstract public String[] getRecipients();
    @Nullable
    abstract public String getLastMessageId();

    @Override
    public RestAction<Void> close() {
        // TODO
        return null;
    }

    @Override
    public ChannelType getType() {
        return ChannelType.DIRECT_MESSAGE;
    }

}
