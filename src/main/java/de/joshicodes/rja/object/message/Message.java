package de.joshicodes.rja.object.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.message.embed.MessageEmbed;
import de.joshicodes.rja.object.user.Masquerade;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.requests.rest.interaction.AddReactionRequest;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.rest.message.MessageEditAction;
import de.joshicodes.rja.rest.message.MessageSendAction;
import de.joshicodes.rja.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {

    public static Message from(RJA rja, JsonObject object, Message message) {

        if(object == null) return null;
        if(!object.has("_id")) {
            return null;
        }

        final String id = JsonUtil.getString(object, "_id", message != null ? message.getId() : null);
        final String nonce = JsonUtil.getString(object, "nonce", message != null ? message.getNonce() : null);
        final String channel = JsonUtil.getString(object, "channel", message != null ? message.getChannelId() : null);
        final String author = JsonUtil.getString(object, "author", message != null ? message.getAuthorId() : null);
        final String content = JsonUtil.getString(object, "content", message != null ? message.getContent() : null);
        final String system = JsonUtil.getString(object, "system", message != null ? message.systemMessage() : null);
        final boolean isSystem = system != null;
        // TODO: attachments
        final String edited = JsonUtil.getString(object, "edited", message != null ? message.getEditedTimestamp() : null);
        final List<MessageEmbed> embeds;
        if(object.has("embeds")) {
            embeds = new ArrayList<>();
            for(JsonElement element : object.get("embeds").getAsJsonArray()) {
                MessageEmbed embed = MessageEmbed.from(rja, element.getAsJsonObject());
                if(embed != null) {
                    embeds.add(embed);
                }
            }
        } else if(message != null) {
            embeds = message.getEmbeds();
        } else embeds = null;

        final List<String> mentions;
        if(object.has("mentions")) {
            mentions = new ArrayList<>();
            for(JsonElement element : object.get("mentions").getAsJsonArray()) {
                mentions.add(element.getAsString());
            }
        } else if (message != null) {
            mentions = message.getMentions();
        } else mentions = null;

        final List<String> replies;
        if(object.has("replies")) {
            replies = new ArrayList<>();
            for(JsonElement element : object.get("replies").getAsJsonArray()) {
                replies.add(element.getAsString());
            }
        } else if (message != null) {
            replies = message.getReplies();
        } else replies = null;

        // TODO: reactions, interactions

        Masquerade m = Masquerade.from(rja, object);
        final Masquerade masquerade;
        if(m == null && message != null) {
            masquerade = message.getMasquerade();
        } else masquerade = m;

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

    public RestAction<GenericChannel> getChannel() {
        return getRJA().retrieveChannel(getChannelId());
    }

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

    public RestAction<Void> react(Emoji emoji) {
        return react(emoji.getId());
    }

    public RestAction<Void> react(String emoji) {
        return new RestAction<>(getRJA()) {
            @Override
            protected Void execute() {
                AddReactionRequest request = new AddReactionRequest(getChannelId(), getId(), emoji);
                getRJA().getRequestHandler().sendRequest(getRJA(), request);
                return null;
            }
        };
    }

}
