package de.joshicodes.rja.object.message;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.rest.RestAction;

import java.util.List;

public class MessageReaction {

    private final RJA rja;

    private final String emojiId;
    private final List<String> reactions;

    MessageReaction(RJA rja, String emojiId, List<String> reactions) {
        this.rja = rja;

        this.emojiId = emojiId;
        this.reactions = reactions;
    }

    void addReaction(String userId) {
        reactions.add(userId);
    }

    void removeReaction(String userId) {
        reactions.remove(userId);
    }

    public boolean contains(String user) {
        return reactions.contains(user);
    }

    public int count() {
        return reactions.size();
    }

    public RJA getRJA() {
        return rja;
    }

    public RestAction<Emoji> retrieveEmoji() {
        return getRJA().retrieveEmoji(emojiId);
    }

    public String getEmojiId() {
        return emojiId;
    }

    public List<String> getReactions() {
        return reactions;
    }
}
