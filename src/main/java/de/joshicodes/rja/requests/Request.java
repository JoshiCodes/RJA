package de.joshicodes.rja.requests;

import java.util.HashMap;

/**
 * Represents a request sent to the Revolt API.
 */
public abstract class Request {

    private final HashMap<String, Object> data;
    private boolean doAuthentication = true;


    Request(String type) {
        this.data = new HashMap<>();
        this.data.put("type", type);
    }

    public void addData(String key, String value) {
        this.data.put(key, value);
    }

    public void addData(String key, int value) {
        this.data.put(key, value);
    }

    public void doAuthentication(boolean doAuthentication) {
        this.doAuthentication = doAuthentication;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public boolean doAuthentication() {
        return doAuthentication;
    }

}
