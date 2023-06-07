package de.joshicodes.rja.requests.rest.user;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.channel.DirectChannel;
import de.joshicodes.rja.requests.rest.RestRequest;

public class OpenDirectMessageRequest extends RestRequest<DirectChannel> {

    public OpenDirectMessageRequest(String userId) {
        super("/users/" + userId + "/dm");
    }

    @Override
    public DirectChannel fetch(RJA rja, int responseCode, JsonElement data) {
        if(!data.isJsonObject())
            return null;
        DirectChannel channel = DirectChannel.from(rja, data.getAsJsonObject());
        rja.cacheChannel(channel);
        return channel;
    }

}
