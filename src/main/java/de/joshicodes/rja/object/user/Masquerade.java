package de.joshicodes.rja.object.user;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.RJAColor;
import de.joshicodes.rja.util.JsonUtil;

public class Masquerade {

    private final String name;
    private final String avatar;
    private final RJAColor color;

    public Masquerade(String name, String avatar, RJAColor color) {
        this.name = name;
        this.avatar = avatar;
        this.color = color;
    }

    public static Masquerade from(RJA rja, JsonObject object) {
        if(object == null) return null;

        final String name = JsonUtil.getString(object, "name", null);
        final String avatar = JsonUtil.getString(object, "avatar", null);
        final RJAColor color = null; // TODO

        return new Masquerade(name, avatar, color);
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public RJAColor getColor() {
        return color;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("avatar", avatar);
        object.addProperty("colour", color.toString());
        return object;
    }

}
