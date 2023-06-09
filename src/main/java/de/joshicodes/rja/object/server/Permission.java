package de.joshicodes.rja.object.server;

public enum Permission {

    /**
     * Manage the channel or channels on the server
     */
    MANAGE_CHANNEL(1),

    /**
     * Manage the server
     */
    MANAGE_SERVER(2),

    /**
     * Manage permissions on servers or channels
     */
    MANAGE_PERMISSIONS(4),

    /**
     * Manage roles on server
     */
    MANAGE_ROLE(8),

    /**
     * Manage emoji on servers
     */
    MANAGE_CUSTOMISATION(16),

    /**
     * Kick other members below their ranking
     */
    KICK_MEMBERS(64),

    /**
     * Ban other members below their ranking
     */
    BAN_MEMBERS(128),

    /**
     * Timeout other members below their ranking
     */
    TIMEOUT_MEMBERS(256),

    /**
     * Assign roles to members below their ranking
     */
    ASSIGN_ROLES(512),


    /**
     * Change own nickname
     */
    CHANGE_NICKNAME(1024),

    /**
     * Change or remove other's nicknames below their ranking
     */
    MANAGE_NICKNAMES(2048),


    /**
     * Change own avatar
     */
    CHANGE_AVATAR(4096),

    /**
     * Remove other's avatars below their ranking
     */
    REMOVE_AVATARS(8192),


    /**
     * View a channel
     */
    VIEW_CHANNEL(1048576),

    /**
     * Read a channel's past message history
     */
    READ_MESSAGE_HISTORY(2097152),

    /**
     * Send a message in a channel
     */
    SEND_MESSAGE(4194304),

    /**
     * Delete messages in a channel
     */
    MANAGE_MESSAGES(8388608),

    /**
     * Manage webhook entries on a channel
     */
    MANAGE_WEBHOOKS(16777216),

    /**
     * Create invites to this channel
     */
    INVITE_OTHERS(33554432),

    /**
     * Send embedded content in this channel
     */
    SEND_EMBEDS(67108864),
    /**
     * Send attachments and media in this channel
     */
    UPLOAD_FILES(134217728),


    /**
     * Masquerade messages using custom nickname and avatar
     */
    MASQUERADE(268435456),

    /**
     * React to messages with emojis
     */
    REACT(536870912),

    /**
     * Connect to a voice channel
     */
    VOICE_CONNECT(1073741824),

    /**
     * Speak in a voice call
     */
    VOICE_SPEAK(2147483648L),

    /**
     * Share video in a voice call
     */
    VOICE_VIDEO(4294967296L),

    /**
     * Mute other members with lower ranking in a voice call
     */
    VOICE_MUTE_MEMBERS(8589934592L),

    /**
     * Deafen other members with lower ranking in a voice call
     */
    VOICE_DEAFEN_MEMBERS(17179869184L),

    /**
     * Move members between voice channels
     */
    VOICE_MOVE_MEMBERS(34359738368L);


    private final long value;
    Permission(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    /**
     * Check if the permission is contained in the value
     * @param value The value to check
     * @return Whether the permission is contained in the value
     */
    public boolean contains(long value) {
        return contains(value, this);
    }

    /**
     * Check if the permission is contained in the value
     * @param value The value to check
     * @param permission The permission to check
     * @return Whether the permission is contained in the value
     */
    public static boolean contains(long value, Permission permission) {
        return (permission.getValue() & value) == permission.getValue();
    }

}
