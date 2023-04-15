package de.joshicodes.rja.object.channel;

public enum ChannelType {
    /**
     * A direct message channel between two users. (Bot and user)
     */
    DIRECT_MESSAGE,

    /**
     * A channel that can be joined by multiple users. Usually owned by a user who can add/remove users.
     */
    GROUP_CHANNEL,

    /**
     * A Channel in a Server.
     * ServerChannel should be overridden by subclasses and never return SERVER_CHANNEL as type.
     * @see ServerChannel
     * @see ServerChannel#getType()
     */
    SERVER_CHANNEL,

    /**
     * A Text Channel in a Server.
     */
    TEXT_CHANNEL;
}
