package de.joshicodes.rja.object;

import com.google.gson.JsonObject;

public abstract class Attachment {

    public static Attachment from(JsonObject object) {

        final String id = object.get("id").getAsString();

        return new Attachment() {
            @Override
            public String getId() {
                return id;
            }
        };

    }

    abstract public String getId();

}
