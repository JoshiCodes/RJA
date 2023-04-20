package de.joshicodes.rja.object.user;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Avatar;
import de.joshicodes.rja.object.channel.DirectChannel;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.enums.RelationShip;
import de.joshicodes.rja.object.message.MessageReceiver;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

public abstract class User {

    abstract public RJA getRJA();

    abstract public String getId();
    abstract public String getUsername();
    abstract public Avatar getAvatar();
    //abstract public Relations getRelations();
    abstract public int getBadges();
    abstract public UserStatus getStatus();
    abstract public UserProfile getProfile();
    abstract public int getFlags();
    abstract public boolean isPrivileged();
    abstract public BotInfo getBotInfo();
    abstract public RelationShip getRelationship();
    abstract public boolean isOnline();

    public String getAsMention() {
        return "<@" + getId() + ">";
    }

    public RestAction<DirectChannel> openPrivateChannel() {
       return getRJA().retrieveDirectChannel(getId());
    }

    public static User from(final RJA rja, JsonObject object) {

        if(object == null) return null;
        if(!object.has("_id") || !object.has("username")) return null;

        final String id = object.get("_id").getAsString();
        final String username = object.get("username").getAsString();
        final Avatar avatar = Avatar.from(object.get("avatar").getAsJsonObject());
        //final Relations relations = Relations.from(object.get("relations").getAsJsonObject());
        final int badges = JsonUtil.getInt(object, "badges", 0);
        final UserStatus status = UserStatus.from(JsonUtil.getObject(object, "status", null));
        final UserProfile profile = UserProfile.from(JsonUtil.getObject(object, "profile", null));
        final int flags = JsonUtil.getInt(object, "flags", 0);
        final boolean privileged = JsonUtil.getBoolean(object, "privileged", false);
        final BotInfo bot = BotInfo.from(JsonUtil.getObject(object, "bot", null));
        final RelationShip relationship = RelationShip.valueOf(JsonUtil.getString(object, "relationship", RelationShip.NONE.name()).toUpperCase());
        final boolean online = JsonUtil.getBoolean(object, "online", false);

        return new User() {

            @Override
            public RJA getRJA() {
                return rja;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public Avatar getAvatar() {
                return avatar;
            }

            @Override
            public int getBadges() {
                return badges;
            }

            @Override
            public UserStatus getStatus() {
                return status;
            }

            @Override
            public UserProfile getProfile() {
                return profile;
            }

            @Override
            public int getFlags() {
                return flags;
            }

            @Override
            public boolean isPrivileged() {
                return privileged;
            }

            @Override
            public BotInfo getBotInfo() {
                return bot;
            }

            @Override
            public RelationShip getRelationship() {
                return relationship;
            }

            @Override
            public boolean isOnline() {
                return online;
            }

        };
    }

}
