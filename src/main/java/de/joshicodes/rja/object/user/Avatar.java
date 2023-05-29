package de.joshicodes.rja.object.user;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.util.JsonUtil;

public abstract class Avatar {

    public static Avatar from(final RJA rja, JsonObject object) {
        if(object == null) return null;
        if(!object.has("_id")) return null;
        final String id = object.get("_id").getAsString();
        final String tag = object.get("tag").getAsString();
        final String fileName = object.get("filename").getAsString();
        final JsonObject metadata = object.get("metadata").getAsJsonObject();
        final String contentType = object.get("content_type").getAsString();
        final int fileSize = object.get("size").getAsInt();

        final boolean deleted = JsonUtil.getBoolean(object, "deleted", false);
        final boolean reported = JsonUtil.getBoolean(object, "reported", false);
        final String messageId = JsonUtil.getString(object, "message_id", null);
        final String userId = JsonUtil.getString(object, "user_id", null);
        final String serverId = JsonUtil.getString(object, "server_id", null);
        final String objectId = JsonUtil.getString(object, "object_id", null);

        return new Avatar() {

            @Override
            public RJA getRJA() {
                return rja;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getTag() {
                return tag;
            }

            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public void getMetadata() {
                // TODO
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public int getFileSize() {
                return fileSize;
            }

            @Override
            public boolean isDeleted() {
                return deleted;
            }

            @Override
            public boolean isReported() {
                return reported;
            }

            @Override
            public String getMessageId() {
                return messageId;
            }

            @Override
            public String getUserId() {
                return userId;
            }

            @Override
            public String getServerId() {
                return serverId;
            }

            @Override
            public String getObjectId() {
                return objectId;
            }
        };

    }

    abstract public RJA getRJA();

    abstract public String getId();
    abstract public String getTag();
    abstract public String getFileName();
    abstract public void getMetadata();  // TODO
    abstract public String getContentType();
    abstract public int getFileSize();

    // Nullable:

    abstract public boolean isDeleted();
    abstract public boolean isReported();
    abstract public String getMessageId();
    abstract public String getUserId();
    abstract public String getServerId();
    abstract public String getObjectId();

    public String getURL() {
        return getURL(0);
    }

    public String getURL(int size) {
        return getRJA().getFileserverUrl() + "/avatars/" + getId() + (size > 0 ? "?size=" + size : "");
    }

}
