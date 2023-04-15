package de.joshicodes.rja.object.channel;

public abstract class ServerChannel extends GenericChannel {

    abstract public String getServerId();

    @Override
    public ChannelType getType() {
        return ChannelType.SERVER_CHANNEL;  // Should be overridden by subclasses
    }

}
