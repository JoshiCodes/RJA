package de.joshicodes.rja.requests.rest.server;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchServerRequest extends RestRequest<Server> {

    public FetchServerRequest(String id) {
        super("/servers/" + id);
    }

    @Override
    public Server fetch(RJA rja, int responseCode, JsonElement data) {
        if(data == null || !data.isJsonObject()) return null;
        return Server.from(rja, data.getAsJsonObject());
    }

}
