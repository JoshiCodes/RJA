package de.joshicodes.rja.requests.packet;

public class BeginTypingRequest extends PacketRequest {

    public BeginTypingRequest(String channel) {
        super("BeginTyping");
        addData("channel", channel);
    }

}
