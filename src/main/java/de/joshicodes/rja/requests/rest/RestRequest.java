package de.joshicodes.rja.requests.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;

import java.util.HashMap;

public abstract class RestRequest<T> {

    private final String method;
    private final String endpoint;

    private HashMap<String, Object> data;
    private HashMap<String, String> headers = null;

    public RestRequest(String endpoint) {
        this("GET", endpoint);
    }

    public RestRequest(String method, String endpoint) {
        this.method = method;
        this.endpoint = endpoint;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasData(String key) {
        return data != null && data.containsKey(key);
    }

    public RestRequest<T> addData(String key, String value) {
        if(data == null) data = new HashMap<>();
        data.put(key, value);
        return this;
    }

    public RestRequest<T> addData(String key, int value) {
        if(data == null) data = new HashMap<>();
        data.put(key, value);
        return this;
    }

    public RestRequest<T> addData(String key, boolean value) {
        if(data == null) data = new HashMap<>();
        data.put(key, value);
        return this;
    }

    public RestRequest<T> addData(String key, float value) {
        if(data == null) data = new HashMap<>();
        data.put(key, value);
        return this;
    }

    public RestRequest<T> addData(String key, JsonObject value) {
        if(data == null) data = new HashMap<>();
        data.put(key, value);
        return this;
    }

    public RestRequest<T> addData(String key, JsonElement value) {
        if(data == null) data = new HashMap<>();
        data.put(key, value);
        return this;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public abstract T fetch(RJA rja, int statusCode, JsonElement data);

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public JsonObject getJsonData() {
        JsonObject object = new JsonObject();
        if(data == null) return null;
        data.forEach((key, value) -> {
            if(value instanceof String s) object.addProperty(key, s);
            else if(value instanceof Integer i) object.addProperty(key, i);
            else if(value instanceof Boolean b) object.addProperty(key, b);
            else if(value instanceof Float f) object.addProperty(key, f);
            else if(value instanceof JsonObject o) object.add(key, o);
            else if(value instanceof JsonElement e) object.add(key, e);
            else object.addProperty(key, value.toString());
        });
        return object;
    }

}
