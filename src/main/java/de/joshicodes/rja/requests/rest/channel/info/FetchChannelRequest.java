package de.joshicodes.rja.requests.rest.channel.info;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchChannelRequest extends RestRequest<GenericChannel> {

    public FetchChannelRequest(String id) {
        super("/channels/" + id);
    }

    @Override
    public GenericChannel fetch(RJA rja, JsonObject data) {
        return rja.cacheChannel(data);
    }

}
