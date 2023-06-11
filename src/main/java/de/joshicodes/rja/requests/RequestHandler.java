package de.joshicodes.rja.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.RJABuilder;
import de.joshicodes.rja.event.Event;
import de.joshicodes.rja.event.EventHandler;
import de.joshicodes.rja.event.EventListener;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.requests.packet.PacketRequest;
import de.joshicodes.rja.requests.packet.PingRequest;
import de.joshicodes.rja.requests.rest.RestRequest;
import de.joshicodes.rja.requests.rest.RestResponse;
import de.joshicodes.rja.util.Pair;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RequestHandler {

    private Thread timerThread;
    private RequestSocket socket;


    final RJABuilder rja;
    private final List<EventListener> listeners;
    private final List<IncomingEvent> events;

    public RequestHandler(RJABuilder rja, List<EventListener> listeners, List<IncomingEvent> events) throws URISyntaxException {
        this.rja = rja;
        this.listeners = listeners;
        this.events = events;
        rja.getLogger().info("Connecting...");
        this.socket = new RequestSocket(this);
    }

    void startTimer() {
        if(timerThread != null) {
            return;
        }
        timerThread = new Thread(() -> {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    PingRequest pingRequest = new PingRequest();
                    sendRequest(pingRequest);
                }
            }, 0, 1000);
        });
        timerThread.start();
    }

    /**
     * Sends a request to the API and returns the result.
     * This method can block the current thread and should be called in a {@link de.joshicodes.rja.rest.RestAction}.
     * @param rja  The RJA instance
     * @param request The request to send
     * @return The result of the request
     * @param <T> The type of the result
     *
     * @deprecated Use {@link #fetchRequest(RJA, RestRequest)} instead
     */
    @Deprecated(forRemoval = true)
    public <T> T sendRequest(final RJA rja, RestRequest<T> request) {
        final RJABuilder builder = this.rja;
        Pair<Integer, JsonElement> multi = builder.makeRequest(request);
        JsonElement e = multi.getSecond();
        int code = multi.getFirst();
        if(e == null && (code < 200 || code >= 300)) {
            return null;
        }
        return request.fetch(rja, multi.getFirst(), e);
    }

    public <T> RestResponse<T> fetchRequest(final RJA rja, RestRequest<T> request) {
        final RJABuilder builder = this.rja;
        Pair<Integer, JsonElement> multi = builder.makeRequest(request);
        JsonElement e = multi.getSecond();
        int code = multi.getFirst();
        if(e == null) {
            return null;
        }
        boolean isRatelimit = code == 429;
        if(isRatelimit) {
            if(e.isJsonObject()) {
                JsonObject o = e.getAsJsonObject();
                if(o.has("retry_after")) {
                    long retryAfter = o.get("retry_after").getAsLong();
                    return new RestResponse<>(null, code, retryAfter);  // Invalid Response, but ratelimited
                }
            }
            return new RestResponse<>(null, code, 0);  // Invalid Response, but the ratelimit is unknown
        }
        return new RestResponse<>(request.fetch(rja, multi.getFirst(), e), code, -1);  // Valid response, no ratelimit
    }

    public void sendRequest(PacketRequest request) {
        JsonObject jsonObject = new JsonObject();
        HashMap<String, Object> data = request.getData();
        for(String key : data.keySet()) {
            Object value = data.get(key);
            if(value instanceof String) {
                jsonObject.addProperty(key, (String) value);
            } else if(value instanceof Integer) {
                jsonObject.addProperty(key, (Integer) value);
            } else if(value instanceof Boolean) {
                jsonObject.addProperty(key, (Boolean) value);
            } else {
                jsonObject.addProperty(key, value.toString());
            }
        }
        try {
            socket.send(jsonObject.toString());
        } catch (WebsocketNotConnectedException e) {
            try {
                tryConnect(5000, true);
            } catch (InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void fireEvent(final Event event) {
        if(listeners == null) {
            return;
        }
        for(EventListener listener : listeners) {
            Class<? extends EventListener> clazz = listener.getClass();
            for(Method method : clazz.getDeclaredMethods()) {
                if(method.getParameterCount() != 1) {
                    // EventListener methods must have exactly one parameter
                    continue;
                }
                if(method.isAnnotationPresent(EventHandler.class)) {
                    Parameter param = method.getParameters()[0];
                    if(param == null) continue;
                    Class<?> p = param.getType();
                    if(p == null) continue;
                    if(!p.equals(event.getClass())) {
                        // EventListener method parameter must be the same as the event class
                        continue;
                    }
                    new Thread(() -> {
                        try {
                            method.setAccessible(true);
                            method.invoke(listener, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            }
        }
    }

    void handleIncoming(RJA instance, String type, JsonObject object) {
        for(IncomingEvent event : events) {
            if(event.getType().equals(type)) {
                IncomingEvent e = event.handle(instance, object);
                if(e == null) continue;
                fireEvent(e);
            }
        }
    }

    public RequestSocket getSocket() {
        return socket;
    }

    void onClose(int i, String s, boolean b) {
        int[] toReconnect = new int[] {
                CloseFrame.ABNORMAL_CLOSE,
                CloseFrame.GOING_AWAY,
                CloseFrame.REFUSE,
                CloseFrame.TLS_ERROR,
                CloseFrame.UNEXPECTED_CONDITION
        };
        if(Arrays.stream(toReconnect).anyMatch(x -> x == i)) {
            rja.getLogger().info("Disconnected from the Revolt API, reconnecting...");
            try {
                tryConnect(5000, true);
            } catch (InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void tryConnect(int timeout, boolean retry) throws InterruptedException, URISyntaxException {
        if(!socket.isClosed())
            socket.close(CloseFrame.NORMAL);
        socket = new RequestSocket(this);
        try {
            socket.connectBlocking(timeout, TimeUnit.MILLISECONDS);
            if(socket.isOpen()) {
                rja.getLogger().info("Connected to the Revolt API!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(retry) {
                rja.getLogger().warning("Failed to connect to the Revolt API, retrying in " + (timeout / 1000) + " seconds...");
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                tryConnect(timeout, true);
            } else {
                rja.getLogger().warning("Failed to connect to the Revolt API!");
                e.printStackTrace();
            }
        }
    }

}
