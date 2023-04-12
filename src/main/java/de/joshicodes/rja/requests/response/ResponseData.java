package de.joshicodes.rja.requests.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;

public record ResponseData(int code, String raw, JsonElement json, Headers headers) {

    public JsonObject getJsonAsObject() {
        return json.getAsJsonObject();
    }

}
