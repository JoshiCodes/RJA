package de.joshicodes.rja.event.server;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.requests.rest.RestResponse;
import de.joshicodes.rja.requests.rest.server.FetchServerRequest;

public class ServerUpdateEvent extends IncomingEvent {

    public ServerUpdateEvent() {
        this(null, null);
    }

    public ServerUpdateEvent(RJA rja, Server server) {
        super(rja, "ServerUpdate");
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {

        String id = object.get("id").getAsString();
        boolean inCache = rja.getServerCache().containsKey(id);
        if(!inCache) {
            // Server not in cache, cannot update with partial data -> fetch full server
            FetchServerRequest request = new FetchServerRequest(id);
            RestResponse<Server> response = rja.getRequestHandler().fetchRequest(rja, request);
            if(response.isOk()) {
                Server server = rja.cacheServer(response.object());
                return new ServerUpdateEvent(rja, server);
            }
        }

        Server server = rja.getServerCache().get(id);
        if(server != null) {
            server.update(object);
        }
        return new ServerUpdateEvent(rja, rja.cacheServer(server));

    }

}
