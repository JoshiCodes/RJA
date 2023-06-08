package de.joshicodes.rja.event.message.reaction;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.message.MessageReaction;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.rest.RestAction;

public class MessageReactEvent extends IncomingEvent {

    public MessageReactEvent() {
        this(null, null, null, null, null);
    }

    private final String messageId;
    private final String channelId;
    private final String userId;
    private final String emojiId;

    public MessageReactEvent(RJA rja, String message, String channel, String user, String emoji) {
        super(rja, "MessageReact");
        this.messageId = message;
        this.channelId = channel;
        this.userId = user;
        this.emojiId = emoji;
    }

    public String getMessageId() {
        return messageId;
    }

    public RestAction<Message> getMessage() {
        return getRJA().retrieveMessage(channelId, messageId, true);
    }

    public MessageReaction getReactions() {
        return getMessage().complete().getReaction(emojiId);
    }

    public String getChannelId() {
        return channelId;
    }

    public RestAction<GenericChannel> getChannel() {
        return getRJA().retrieveChannel(channelId);
    }

    public String getUserId() {
        return userId;
    }

    public RestAction<User> getUser() {
        return getRJA().retrieveUser(userId);
    }

    public String getEmojiId() {
        return emojiId;
    }

    public RestAction<Emoji> getEmoji() {
        return getRJA().retrieveEmoji(emojiId);
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {

        String message = object.get("id").getAsString();
        String channel = object.get("channel_id").getAsString();
        String user = object.get("user_id").getAsString();

        Message old = rja.retrieveMessage(channel, messageId).complete();
        if(old.getReaction(emojiId) != null) {
            if(old.getReaction(emojiId).contains(user)) {
                return null;  // User has already reacted with this emoji, so this event is invalid
            }
        }
        rja.getMessageCache().remove(messageId);  // remove message from cache, as it could be outdated

        if(user.equals(rja.retrieveSelfUser().complete().getId())) return null;

        String emoji = object.get("emoji_id").getAsString();

        return new MessageReactEvent(rja, message, channel, user, emoji);

    }

}
