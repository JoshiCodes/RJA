package de.joshicodes.rja.object.channel;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.message.MessageReceiver;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.util.JsonUtil;

public abstract class GenericChannel extends MessageReceiver {

    public static GenericChannel from(final RJA rja, final JsonObject object) {
        final String type = JsonUtil.getString(object, "channel_type", null);
        if(type == null) {
            return null;
        }
        switch(type) {
            case "DirectMessage":
                return DirectChannel.from(rja, object);
            case "Group":
                return GroupChannel.from(rja, object); // TODO
            case "TextChannel":
                return TextChannel.from(rja, object);
            case "VoiceChannel":
                //return VoiceChannel.from(rja, object); // TODO
                break;
            // "SavedMessages" is ignored.
            default:
                return null;
        }
        return null;
    }

    abstract public RJA getRJA();
    abstract public String getId();

    abstract public RestAction<Void> close();

    abstract public ChannelType getType();

}
