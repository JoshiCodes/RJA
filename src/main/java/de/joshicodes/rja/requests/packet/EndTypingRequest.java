package de.joshicodes.rja.requests.packet;

public class EndTypingRequest extends PacketRequest {

    public EndTypingRequest(String channel) {
        super("EndTyping");
        addData("channel", channel);
    }

}
