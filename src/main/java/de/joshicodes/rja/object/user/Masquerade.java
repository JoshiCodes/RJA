package de.joshicodes.rja.object.user;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.RJAColor;
import de.joshicodes.rja.util.JsonUtil;

public record Masquerade(String name, String avatar, RJAColor color) {

    public static Masquerade from(RJA rja, JsonObject object) {
        if (object == null) return null;

        final String name = JsonUtil.getString(object, "name", null);
        final String avatar = JsonUtil.getString(object, "avatar", null);
        final RJAColor color = null; // TODO

        return new Masquerade(name, avatar, color);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("avatar", avatar);
        object.addProperty("colour", color.toString());
        return object;
    }

}
