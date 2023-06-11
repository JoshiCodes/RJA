package de.joshicodes.rja.object.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.requests.rest.server.member.FetchAllMembersRequest;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
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
        final List<Role> roles = new ArrayList<>();
        if(object.has("roles")) {
            JsonObject rolesObject = object.getAsJsonObject("roles");
            for(String roleId : rolesObject.keySet()) {
                JsonObject roleObject = rolesObject.getAsJsonObject(roleId);
                if(roleObject == null) continue;
                Role role = Role.from(rja, roleId, roleObject);
                roles.add(role);
            }
        }

        Server server = new Server(rja, id, ownerId);
        server.name = name;
        server.description = description;
        server.channels = channels;
        server.roles = roles;

        server.icon = new Icon(rja, server, JsonUtil.getObject(object, "icon", null));

        return server;

    }

    private final RJA rja;
    private final String id;
    private final String ownerId;

    private String name;
    private String description;
    private List<String> channels;

    private List<Role> roles;

    private Icon icon;

    Server(final RJA rja, final String id, final String owner) {
        this.rja = rja;
        this.id = id;
        this.ownerId = owner;
    }

    public RestAction<Member> retrieveMember(User user) {
        return rja.retrieveMember(this, user);
    }

    /**
     * Fetches all members of this server.
     * <b>Note:</b> The resulting {@link HashMap} can get very large, depending on the size of the server.
     *              If you need a specific member, use {@link #retrieveMember(User)} instead.
     * @param excludeOffline Whether to exclude offline members.
     * @return A {@link RestAction} that retrieves a {@link HashMap} of {@link User}s and {@link Member}s.
     */
    public RestAction<HashMap<User, Member>> retrieveAllMembers(final boolean excludeOffline) {
        return new RestAction<>(rja, () -> new FetchAllMembersRequest(id, excludeOffline));
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

    public List<Role> getRoles() {
        return roles;
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

    public Icon getIcon() {
        return icon;
    }

    public Role getRole(String roleId) {
        return roles.stream().filter(r -> r.getId().equals(roleId)).findFirst().orElse(null);
    }

    public static class Icon {

        private final RJA rja;
        private final Server server;
        private final JsonObject object;

        private final String id;
        private final String tag;
        private final String filename;

        private final String contentType;
        private final int size;
        private final boolean deleted;
        private final boolean reported;

        private final String messageId;
        private final String userId;
        private final String serverId;
        private final String objectId;

        Icon(RJA rja, Server server, JsonObject object) {
            this.rja = rja;
            this.server = server;
            this.object = object;

            this.id = JsonUtil.getString(object, "_id", null);
            this.tag = JsonUtil.getString(object, "tag", null);
            this.filename = JsonUtil.getString(object, "filename", null);
            // metadata here
            this.contentType = JsonUtil.getString(object, "content_type", null);
            this.size = JsonUtil.getInt(object, "size", 0);
            this.deleted = JsonUtil.getBoolean(object, "deleted", false);
            this.reported = JsonUtil.getBoolean(object, "reported", false);
            this.messageId = JsonUtil.getString(object, "message_id", null);
            this.userId = JsonUtil.getString(object, "user_id", null);
            this.serverId = JsonUtil.getString(object, "server_id", null);
            this.objectId = JsonUtil.getString(object, "object_id", null);

        }

        public RJA getRJA() {
            return rja;
        }

        public Server getServer() {
            return server;
        }

        public String getId() {
            return id;
        }

        public String getTag() {
            return tag;
        }

        public String getFilename() {
            return filename;
        }

        public String getContentType() {
            return contentType;
        }

        public int getSize() {
            return size;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public boolean isReported() {
            return reported;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getUserId() {
            return userId;
        }

        public String getServerId() {
            return serverId;
        }

        public String getObjectId() {
            return objectId;
        }

        public String getURL() {
            return getURL(0);
        }

        public String getURL(int size) {
            return getRJA().getFileserverUrl() + "/icons/" + getId() + (size > 0 ? "?size=" + size : "");
        }

    }

}
