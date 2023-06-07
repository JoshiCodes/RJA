package de.joshicodes.rja.requests.rest.interaction;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchEmojiRequest extends RestRequest<Emoji> {

    public FetchEmojiRequest(String id) {
        super("GET", "/custom/emoji/" + id);
        addData("id", id);
    }

    @Override
    public Emoji fetch(RJA rja, int statusCode, JsonElement data) {
        Emoji e = Emoji.from(rja, data.getAsJsonObject());
        if(e == null) return null;
        rja.cacheEmoji(e);
        return e;
    }

}
