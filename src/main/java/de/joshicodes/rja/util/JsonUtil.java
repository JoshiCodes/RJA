package de.joshicodes.rja.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {

    public static String getString(JsonObject object, String key, String def) {
        if(object == null) return def;
        if(object.has(key)) {
            JsonElement element = object.get(key);
            if(element.isJsonPrimitive()) {
                return element.getAsString();
            }
        }
        return def;
    }

    public static int getInt(JsonObject object, String key, int i) {
        if(object == null) return i;
        if(object.has(key)) {
            JsonElement element = object.get(key);
            if(element.isJsonPrimitive()) {
                return element.getAsInt();
            }
        }
        return i;
    }

    public static boolean getBoolean(JsonObject object, String key, boolean b) {
        if(object == null) return b;
        if(object.has(key)) {
            JsonElement element = object.get(key);
            if(element.isJsonPrimitive()) {
                return element.getAsBoolean();
            }
        }
        return b;
    }

    public static JsonObject getObject(JsonObject object, String key, JsonObject o) {
        if(object == null) return o;
        if(object.has(key)) {
            JsonElement element = object.get(key);
            if(element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        }
        return o;
    }

    public static JsonArray getArray(JsonObject object, String key, JsonArray def) {
        if(object == null) return def;
        if(object.has(key)) {
            JsonElement element = object.get(key);
            if(element.isJsonArray()) {
                return element.getAsJsonArray();
            }
        }
        return def;
    }

}
