package de.joshicodes.rja.exception;

import de.joshicodes.rja.requests.rest.RestRequest;
import de.joshicodes.rja.rest.RestAction;

public class RatelimitException extends RuntimeException {

    private final RestAction<?> action;
    private final RestRequest<?> request;

    public RatelimitException(RestAction<?> action, RestRequest<?> request) {
        super("Ratelimit reached for " + request.getEndpoint() + " (" + request.getMethod() + ")");
        this.action = action;
        this.request = request;
    }

    public RestAction<?> getAction() {
        return action;
    }

    public RestRequest<?> getRequest() {
        return request;
    }

}
