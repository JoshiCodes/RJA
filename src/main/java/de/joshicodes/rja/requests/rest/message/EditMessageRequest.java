package de.joshicodes.rja.requests.rest.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.requests.rest.RestRequest;

import javax.annotation.Nullable;
import java.util.List;

public class EditMessageRequest extends RestRequest<Message> {

    public EditMessageRequest(final Message message, final @Nullable String content, final @Nullable List<MessageEmbed> embeds) {
        super("PATCH", "/channels/" + message.getChannelId() + "/messages/" + message.getId());

        if(content != null && !content.isEmpty() && !content.isBlank() && !message.getContent().equals(content))
            addData("content", content);

        if(embeds != null && !embeds.isEmpty() && !message.getEmbeds().equals(embeds)) {
            JsonArray array = new JsonArray();
            embeds.forEach(embed -> array.add(embed.toJson()));
            addData("embeds", array);
        }

    }

    @Override
    public Message fetch(RJA rja, JsonElement data) {
        if (!data.isJsonObject())
            return null;
        return Message.from(rja, data.getAsJsonObject());
    }

}
