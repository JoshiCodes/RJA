package de.joshicodes.rja.object.channel;

import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.rest.RestAction;

public abstract class ServerChannel extends GenericChannel {

    abstract public String getServerId();

    public RestAction<Server> getServer() {
        return getRJA().retrieveServer(getServerId());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.SERVER_CHANNEL;  // Should be overridden by subclasses
    }

}
