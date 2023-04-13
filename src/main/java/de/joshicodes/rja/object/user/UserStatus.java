package de.joshicodes.rja.object.user;

import com.google.gson.JsonObject;
import de.joshicodes.rja.util.JsonUtil;

public abstract class UserStatus {

    abstract public String text();
    abstract public Presence presence();

    public static UserStatus from(JsonObject object) {
        if(object == null) return null;

        final String text = JsonUtil.getString(object, "text", null);
        final Presence presence = Presence.fromString(JsonUtil.getString(object, "presence", null), Presence.ONLINE);

        return new UserStatus() {
            @Override
            public String text() {
                return text;
            }

            @Override
            public Presence presence() {
                return presence;
            }
        };
    }

    public enum Presence {
        ONLINE,
        IDLE,
        FOCUS,
        BUSY,
        INVISIBLE;

        public static Presence fromString(String presence) {
            return fromString(presence, null);
        }

        public static Presence fromString(String presence, Presence def) {
            if(presence != null) {
                for(Presence p : values()) {
                    if(p.name().equalsIgnoreCase(presence)) {
                        return p;
                    }
                }
            }
            return def;
        }

        private final String friendlyName;
        Presence() {
            this.friendlyName = name().charAt(0) + name().substring(1).toLowerCase();
        }

        Presence(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

    }

}
