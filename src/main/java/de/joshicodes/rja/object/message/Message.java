package de.joshicodes.rja.object.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.object.user.Masquerade;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.rest.message.MessageEditAction;
import de.joshicodes.rja.rest.message.MessageSendAction;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {

    public static Message from(RJA rja, JsonObject object) {

        if(object == null) return null;
        if(!object.has("_id")) {
            return null;
        }

        final String id = object.get("_id").getAsString();
        final String nonce = JsonUtil.getString(object, "nonce", null);
        final String channel = JsonUtil.getString(object, "channel", null);
        final String author = JsonUtil.getString(object, "author", null);
        final String content = JsonUtil.getString(object, "content", null);
        final String system = JsonUtil.getString(object, "system", null);
        final boolean isSystem = system != null;
        // TODO: attachments
        final String edited = JsonUtil.getString(object, "edited", null);
        final List<MessageEmbed> embeds;
        if(object.has("embeds")) {
            embeds = new ArrayList<>();
            for(JsonElement element : object.get("embeds").getAsJsonArray()) {
                MessageEmbed embed = MessageEmbed.from(rja, element.getAsJsonObject());
                if(embed != null) {
                    embeds.add(embed);
                }
            }
        } else embeds = null;

        final List<String> mentions;
        if(object.has("mentions")) {
            mentions = new ArrayList<>();
            for(JsonElement element : object.get("mentions").getAsJsonArray()) {
                mentions.add(element.getAsString());
            }
        } else mentions = null;

        final List<String> replies;
        if(object.has("replies")) {
            replies = new ArrayList<>();
            for(JsonElement element : object.get("replies").getAsJsonArray()) {
                replies.add(element.getAsString());
            }
        } else replies = null;

        // TODO: reactions, interactions

        final Masquerade masquerade = Masquerade.from(rja, object);

        return new Message() {

            @Override
            public RJA getRJA() {
                return rja;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getNonce() {
                return nonce;
            }

            @Override
            public String getChannelId() {
                return channel;
            }

            @Override
            public String getAuthorId() {
                return author;
            }

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public String systemMessage() {
                return system;
            }

            @Override
            public boolean isSystem() {
                return isSystem;
            }

            @Override
            public String getEditedTimestamp() {
                return edited;
            }

            @Override
            public List<MessageEmbed> getEmbeds() {
                return embeds;
            }

            @Override
            public List<String> getMentions() {
                return mentions;
            }

            @Override
            public List<String> getReplies() {
                return replies;
            }

            @Override
            public Masquerade getMasquerade() {
                return masquerade;
            }
        };

    }

    abstract public RJA getRJA();

    abstract public String getId();
    abstract public String getNonce();

    abstract public String getChannelId();

    public RestAction<GenericChannel> retrieveChannel() {
        return getRJA().retrieveChannel(getChannelId());
    }

    abstract public String getAuthorId();

    public RestAction<User> retrieveAuthor() {
        return getRJA().retrieveUser(getAuthorId());
    }

    abstract public String getContent();

    /**
     * Returns the system type of the message.
     * If {@link #isSystem()} is false, this will return null.
     * @return The system type of the message or null.
     */
    abstract public String systemMessage();
    abstract public boolean isSystem();

    abstract public String getEditedTimestamp(); // TODO: String -> Data

    abstract public List<MessageEmbed> getEmbeds();
    abstract public List<String> getMentions();
    abstract public List<String> getReplies();

    abstract public Masquerade getMasquerade();

    public MessageSendAction reply(String content, boolean mention) {
        return getRJA().retrieveChannel(getChannelId()).complete().sendMessage(content).addReply(getId(), mention);
    }

    public MessageSendAction reply(String content) {
        return reply(content, false);
    }

    public MessageSendAction reply(boolean mention) {
        return getRJA().retrieveChannel(getChannelId()).complete().sendMessage(null).addReply(getId(), mention);
    }

    public MessageEditAction edit(final String content) {
        return new MessageEditAction(getRJA(), this).setContent(content);
    }

    public MessageEditAction edit(final MessageEmbed embed) {
        return new MessageEditAction(getRJA(), this).addEmbed(embed);
    }

    public MessageEditAction edit(final MessageEmbed... embeds) {
        return new MessageEditAction(getRJA(), this).setEmbeds(embeds);
    }

}
