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
import de.joshicodes.rja.requests.rest.interaction.RemoveReactionRequest;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.rest.message.MessageEditAction;
import de.joshicodes.rja.rest.message.MessageSendAction;
import de.joshicodes.rja.util.JsonUtil;

import javax.annotation.Nullable;
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

        final List<MessageReaction> reactions;
        if(object.has("reactions")) {

            reactions = new ArrayList<>();

            JsonObject reactionsObject = object.get("reactions").getAsJsonObject();
            for(String key : reactionsObject.keySet()) {

                List<String> users = new ArrayList<>();
                for(JsonElement element : reactionsObject.get(key).getAsJsonArray()) {
                    users.add(element.getAsString());
                }
                reactions.add(new MessageReaction(rja, key, users));
            }

        } else if (message != null) {
            reactions = message.getReactions();
        } else reactions = new ArrayList<>();

        // TODO: interactions

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
            public List<MessageReaction> getReactions() {
                return reactions;
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
    abstract public List<MessageReaction> getReactions();

    abstract public Masquerade getMasquerade();

    public RestAction<GenericChannel> getChannel() {
        return getRJA().retrieveChannel(getChannelId());
    }

    public MessageSendAction reply(MessageEmbed embed, boolean mention) {
        return getRJA().retrieveChannel(getChannelId()).complete().sendEmbeds(embed).addReply(getId(), mention);
    }

    public MessageSendAction reply(MessageEmbed embed) {
        return reply(embed, false);
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

    /**
     * Gets the reaction of the given emoji.
     * @param emoji The emoji to get the reaction of.
     * @return The reaction of the given emoji or null if there is no reaction for the given emoji.
     */
    public MessageReaction getReaction(String emoji) {
        return getReactions().stream().filter(r -> r.getEmojiId().equals(emoji)).findFirst().orElse(null);
    }

    /**
     * Removes a random reaction of the given emoji.
     * @param emoji The emoji to remove the reaction of.
     * @return A RestAction that completes with the removed reaction. If there is no reaction for the given emoji afterward, this will complete with null.
     */
    public RestAction<MessageReaction> removeReaction(Emoji emoji) {
        return removeReaction(emoji.getId());
    }

    /**
     * Removes a random reaction of the given emoji.
     * @param emoji The emoji to remove the reaction of.
     * @return A RestAction that completes with the removed reaction. If there is no reaction for the given emoji afterward, this will complete with null.
     */
    public RestAction<MessageReaction> removeReaction(String emoji) {
        return removeReaction(emoji, null, false);
    }

    /**
     * Removes a reaction of the given emoji by the given user.
     * @param emoji The emoji to remove the reaction of.
     * @param user The user to remove the reaction of.
     * @return A RestAction that completes with the removed reaction. If there is no reaction for the given emoji and user afterward, this will complete with null.
     */
    public RestAction<MessageReaction> removeReaction(Emoji emoji, String user) {
        return removeReaction(emoji.getId(), user);
    }

    /**
     * Removes a reaction of the given emoji by the given user.
     * @param emoji The emoji to remove the reaction of.
     * @param user The user to remove the reaction of.
     * @return A RestAction that completes with the removed reaction. If there is no reaction for the given emoji and user afterward, this will complete with null.
     */
    public RestAction<MessageReaction> removeReaction(String emoji, String user) {
        return removeReaction(emoji, user, false);
    }

    /**
     * Removes all reactions of the given emoji.
     * @param emoji The emoji to remove the reactions of.
     * @param removeAll Whether to remove all reactions or just one.
     * @return A RestAction that completes with the removed reaction. If there is no reaction for the given emoji afterward, this will complete with null.
     */
    public RestAction<MessageReaction> removeReaction(Emoji emoji, boolean removeAll) {
        return removeReaction(emoji.getId(), removeAll);
    }

    /**
     * Removes all reactions of the given emoji.
     * @param emoji The emoji to remove the reactions of.
     * @param removeAll Whether to remove all reactions or just one.
     * @return A RestAction that completes with the removed reaction. If there is no reaction for the given emoji afterward, this will complete with null.
     */
    public RestAction<MessageReaction> removeReaction(String emoji, boolean removeAll) {
        return removeReaction(emoji, null, removeAll);
    }

    RestAction<MessageReaction> removeReaction(Emoji emoji, String user, boolean removeAll) {
        return removeReaction(emoji.getId(), user, removeAll);
    }

    RestAction<MessageReaction> removeReaction(String emoji, @Nullable String user, boolean removeAll) {
        return new RestAction<>(getRJA()) {
            @Override
            protected MessageReaction execute() {
                RemoveReactionRequest request = new RemoveReactionRequest(getChannelId(), getId(), emoji, user, removeAll);
                getRJA().getRequestHandler().sendRequest(getRJA(), request);
                if(getReaction(emoji) != null) {
                    if(removeAll) {
                        getReaction(emoji).removeReaction(user);
                    } else {
                        if(user == null) {
                            // We do not know which user got removed, so we have to fetch the message again
                            // Cannot retrieve reactions from a message alone, so we have to remove it from the cache and retrieve it again
                            getRJA().getMessageCache().remove(getId());
                            Message updated = getRJA().retrieveMessage(getChannelId(), getId()).complete(); // Retrieve the message again (this will cache it if enabled)

                            // This Message instance is now outdated, so we have to update it
                            getReactions().stream().filter(r -> r.getEmojiId().equals(emoji)).findFirst().ifPresent(r -> {
                                getReactions().remove(r);
                                MessageReaction updatedReaction = updated.getReaction(emoji);
                                if(updatedReaction != null) getReactions().add(updatedReaction);
                            });
                        } else {
                            getReaction(emoji).removeReaction(user);
                        }
                    }
                }
                return getReaction(emoji);
            }
        };
    }

    public RestAction<MessageReaction> react(Emoji emoji) {
        return react(emoji.getId());
    }

    public RestAction<MessageReaction> react(String emoji) {
        return new RestAction<>(getRJA()) {
            @Override
            protected MessageReaction execute() {
                AddReactionRequest request = new AddReactionRequest(getChannelId(), getId(), emoji);
                getRJA().getRequestHandler().sendRequest(getRJA(), request);
                MessageReaction reaction = getReaction(emoji);
                reaction.addReaction(getRJA().retrieveSelfUser().complete().getId());
                return reaction;
            }
        };
    }

}
