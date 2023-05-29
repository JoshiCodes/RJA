package de.joshicodes.rja.requests.rest.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Attachment;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.requests.rest.RestRequest;

import javax.annotation.Nullable;
import java.util.List;

public class EditMessageRequest extends RestRequest<Message> {

    public EditMessageRequest(final Message message, final @Nullable String content, final @Nullable List<MessageEmbed> embeds, final @Nullable List<Attachment> attachments) {
        super("PATCH", "/channels/" + message.getChannelId() + "/messages/" + message.getId());

        if((message.getContent() == null && content != null) || ((content != null && message.getContent() != null) && !content.isEmpty() && !content.isBlank() && !message.getContent().equals(content)))
            addData("content", content);

        if((message.getEmbeds() == null && embeds != null) || ((embeds != null && message.getEmbeds() != null) && !embeds.isEmpty() && !message.getEmbeds().equals(embeds))) {
            JsonArray array = new JsonArray();
            embeds.forEach(embed -> array.add(embed.toJson()));
            addData("embeds", array);
        }

        if(attachments != null && !attachments.isEmpty()) {
            JsonArray array = new JsonArray();
            attachments.forEach(attachment -> array.add(attachment.getId()));
            addData("attachments", array);
        }

    }

    @Override
    public Message fetch(RJA rja, JsonElement data) {
        if (!data.isJsonObject())
            return null;
        return Message.from(rja, data.getAsJsonObject());
    }

}
