package de.joshicodes.rja.requests.rest;

public record RestResponse<T>(T object, int code, long retryAfter) {

    public boolean isOk() {
        return code >= 200 && code < 300 && !isRatelimited() && object != null;
    }

    public boolean isRatelimited() {
        return retryAfter != -1;
    }

}
