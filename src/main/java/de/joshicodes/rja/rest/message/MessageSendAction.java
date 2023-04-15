package de.joshicodes.rja.rest.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.object.user.Masquerade;
import de.joshicodes.rja.requests.rest.message.MessageSendRequest;
import de.joshicodes.rja.rest.RestAction;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MessageSendAction extends RestAction<Message> {

    private final String receiver;

    private String content;
    private List<String> attachments;
    private HashMap<String, Boolean> replies;
    private List<MessageEmbed> embeds;
    private Masquerade masquerade;
    // TODO: Interactions

    public MessageSendAction(final RJA rja, final String receiver) {
        super(rja);
        this.receiver = receiver;
    }

    public MessageSendAction setContent(String content) {
        this.content = content;
        return this;
    }

    public MessageSendAction setAttachments(List<String> attachments) {
        this.attachments = attachments;
        return this;
    }

    public MessageSendAction setReplies(HashMap<String, Boolean> replies) {
        this.replies = replies;
        return this;
    }

    public MessageSendAction addReply(String messageId, boolean mention) {
        if(replies == null)
            replies = new HashMap<>();
        replies.put(messageId, mention);
        return this;
    }

    public MessageSendAction setEmbeds(MessageEmbed... embeds) {
        this.embeds = List.of(embeds);
        return this;
    }

    public MessageSendAction setMasquerade(Masquerade masquerade) {
        this.masquerade = masquerade;
        return this;
    }

    @Override
    protected Message execute() {

        MessageSendRequest request = new MessageSendRequest(receiver);
        if(content != null)
            request.addData("content", content);

        if(attachments != null) {
            JsonArray attachments = new JsonArray();
            for(String attachment : this.attachments) {
                attachments.add(attachment);
            }
            request.addData("attachments", attachments);
        }

        if(replies != null) {
            JsonArray replies = new JsonArray();
            for(String reply : this.replies.keySet()) {
                JsonObject replyObject = new JsonObject();
                replyObject.addProperty("id", reply);
                replyObject.addProperty("mention", this.replies.get(reply));
                replies.add(replyObject);
            }
            request.addData("replies", replies);
        }

        if(embeds != null) {
            JsonArray embeds = new JsonArray();
            for(MessageEmbed embed : this.embeds) {
                embeds.add(embed.toJson());
            }
            request.addData("embeds", embeds);
        }

        if(masquerade != null) {
            request.addData("masquerade", masquerade.toJson());
        }

        UUID nonce = UUID.randomUUID();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Idempotency-Key", nonce.toString());
        request.setHeaders(headers);

        return getRJA().getRequestHandler().sendRequest(getRJA(), request);
    }

}
