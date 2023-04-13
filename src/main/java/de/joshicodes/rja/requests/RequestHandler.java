package de.joshicodes.rja.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.RJABuilder;
import de.joshicodes.rja.event.Event;
import de.joshicodes.rja.event.EventHandler;
import de.joshicodes.rja.event.EventListener;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.requests.packet.PacketRequest;
import de.joshicodes.rja.requests.packet.PingRequest;
import de.joshicodes.rja.requests.rest.RestRequest;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.HttpUtil;
import de.joshicodes.rja.util.JsonUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class RequestHandler extends WebSocketClient {

    private Thread timerThread;

    private final RJABuilder rja;
    private final List<EventListener> listeners;
    private final List<IncomingEvent> events;

    public RequestHandler(RJABuilder rja, List<EventListener> listeners, List<IncomingEvent> events) throws URISyntaxException {
        super(new URI(rja.getWSUrl()));
        this.rja = rja;
        this.listeners = listeners;
        this.events = events;
        rja.getLogger().info("Connecting...");
    }

    private void startTimer() {
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
     * This method can block the current thread and should be called in a {@link RestAction}.
     * @param rja  The RJA instance
     * @param request The request to send
     * @return The result of the request
     * @param <T> The type of the result
     */
    public <T> T sendRequest(final RJA rja, RestRequest<T> request) {
        final RJABuilder builder = this.rja;
        JsonElement e = builder.makeRequest(request);
        if(e == null) {
            return null;
        }
        return request.fetch(rja, e);
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
        send(jsonObject.toString());
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

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        startTimer();
        rja.getLogger().info("Connected to the Revolt API!");
    }

    @Override
    public void onMessage(String s) {
        //rja.getLogger().info("Received message: " + s);
        if(!s.startsWith("{")) {
            // Not a JSON object
            return;
        }
        JsonElement element = JsonParser.parseString(s);
        if(!element.isJsonObject()) {
            // Not a JSON object
            return;
        }
        JsonObject object = element.getAsJsonObject();
        if(!object.has("type")) {
            // No type
            return;
        }
        RJA instance = rja.get();
        if(instance == null) {
            rja.getLogger().warning("Cannot handle incoming event, RJA instance is null!");
            return;
        }
        String type = object.get("type").getAsString();
        if(type.equals("Bulk")) {
            JsonArray array = JsonUtil.getArray(object, "data", null);
            if(array != null) {
                for(JsonElement e : array) {
                    if(!e.isJsonObject()) {
                        continue;
                    }
                    JsonObject o = e.getAsJsonObject();
                    if(!o.has("type")) {
                        continue;
                    }
                    String t = o.get("type").getAsString();
                    handleIncoming(instance, t, o);
                }
                return;
            }
        }
        handleIncoming(instance, type, object);
    }

    private void handleIncoming(RJA instance, String type, JsonObject object) {
        for(IncomingEvent event : events) {
            if(event.getType().equals(type)) {
                IncomingEvent e = event.handle(instance, object);
                if(e == null) continue;
                fireEvent(e);
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

}
