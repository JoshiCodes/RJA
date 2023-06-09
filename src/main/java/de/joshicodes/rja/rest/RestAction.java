package de.joshicodes.rja.rest;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.exception.RatelimitException;
import de.joshicodes.rja.requests.RequestHandler;
import de.joshicodes.rja.requests.rest.RestRequest;
import de.joshicodes.rja.requests.rest.RestResponse;
import de.joshicodes.rja.util.Pair;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RestAction<R> {

    public static final int MAX_ATTEMPTS = 4;

    private final RJA rja;
    protected Supplier<RestRequest<R>> request;

    public RestAction(RJA rja, Supplier<RestRequest<R>> request) {
        this.rja = rja;
        this.request = request;
    }

    protected Pair<Long, R> execute() throws Exception {
        RequestHandler handler = rja.getRequestHandler();
        RestResponse<R> response = handler.fetchRequest(rja, request.get());
        if(response == null) {
            return null;
        }
        return new Pair<>(response.retryAfter(), response.object());
    }

    public R complete() {
        int attempts = 0;
        long retryAfter = 0;
        while (retryAfter != -1 && attempts < MAX_ATTEMPTS) {
            try {
                Pair<Long, R> result = execute();
                if(result == null) {
                    // Failed to get a result, throw an exception
                    throw new RatelimitException(this, request.get());
                }
                retryAfter = result.getFirst();
                if(retryAfter == -1) {
                    // Successfully got a result, call the success consumer and return
                    return result.getSecond();
                }
            } catch (Exception e) {
                // Failed to get a result
                e.printStackTrace();
                continue;
            }
            // Got ratelimited, wait and try again
            try {
                Thread.sleep(retryAfter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempts++;
        }
        throw new RatelimitException(this, request.get());
    }

    public void queue() {
        queue(null, null);
    }

    public void queue(Consumer<R> success) {
        queue(success, null);
    }

    public void queue(Consumer<R> success, Consumer<Throwable> failure) {
       rja.getRequestHandler().queueRequest(this, success, failure);
    }

    public RJA getRJA() {
        return rja;
    }

}
