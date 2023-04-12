package de.joshicodes.rja.object;

public abstract class Message {

    abstract public String getId();
    abstract public String getNonce();
    abstract public String getChannel();
    abstract public User getAuthor();
    abstract public String getContent();

}
