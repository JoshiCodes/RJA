package de.joshicodes.rja.requests.packet;

import de.joshicodes.rja.requests.rest.RestRequest;

import java.util.HashMap;

/**
 * Represents a request sent to the Revolt API.
 * Usually used to send a request through the WebSocket Connection.
 * To send HTTP requests, use {@link RestRequest}.
 */
public abstract class PacketRequest {

    private final HashMap<String, Object> data;

    public PacketRequest(String type) {
        this.data = new HashMap<>();
        this.data.put("type", type);
    }

    public void addData(String key, String value) {
        this.data.put(key, value);
    }

    public void addData(String key, int value) {
        this.data.put(key, value);
    }

    public HashMap<String, Object> getData() {
        return data;
    }

}
