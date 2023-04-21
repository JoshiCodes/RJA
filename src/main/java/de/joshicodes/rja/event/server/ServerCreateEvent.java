package de.joshicodes.rja.event.server;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.channel.ServerChannel;
import de.joshicodes.rja.object.server.Server;

/**
 * This Event gets fired, when the BotUser joins a new Server.
 */
public class ServerCreateEvent extends IncomingEvent {

    public ServerCreateEvent() {
        this(null, null);
    }

    private final Server server;

    public ServerCreateEvent(RJA rja, Server server) {
        super(rja, "ServerCreate");
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {
        if(!object.has("server")) return null;
        JsonObject serverObject = object.getAsJsonObject("server");
        if(serverObject == null) return null;
        Server server = rja.cacheServer(Server.from(rja, serverObject));
        if(server == null) return null;
        return new ServerCreateEvent(rja, server);
    }

}
