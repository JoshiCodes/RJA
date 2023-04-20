package de.joshicodes.rja.event.server;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.cache.Cache;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.server.Server;

public class ServerDeleteEvent extends IncomingEvent {

    public ServerDeleteEvent() {
        this(null, null);
    }

    private final String id;

    public ServerDeleteEvent(RJA rja, String id) {
        super(rja, "ServerDelete");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {
        String id = object.get("id").getAsString();
        Cache<Server> cache = rja.getServerCache();
        if(cache != null)
            cache.stream().filter(server -> server.getId().equals(id)).forEach(cache::remove);
        return new ServerDeleteEvent(rja, id);
    }

}
