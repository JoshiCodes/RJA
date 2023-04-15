package de.joshicodes.rja.object.server;

import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.channel.ServerChannel;

import java.util.List;

public abstract class Server {

    abstract public String getId();
    abstract public String getOwnerId();
    abstract public String getName();
    abstract public String getDescription();

    abstract public List<ServerChannel> getChannels();
    abstract public List<Emoji> getEmojis();

}
