package de.joshicodes.rja.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.RJABuilder;
import de.joshicodes.rja.util.JsonUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RequestSocket extends WebSocketClient {

    private final RequestHandler handler;
    private final RJABuilder rja;

    RequestSocket(RequestHandler handler) throws URISyntaxException {
        super(new URI(handler.rja.getWSUrl()));
        this.handler = handler;
        this.rja = handler.rja;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        handler.startTimer();
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
                    handler.handleIncoming(instance, t, o);
                }
                return;
            }
        }
        handler.handleIncoming(instance, type, object);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        handler.onClose(i, s, b);
    }

    @Override
    public void onError(Exception e) {

    }

}
