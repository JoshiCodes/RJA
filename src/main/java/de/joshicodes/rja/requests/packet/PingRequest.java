package de.joshicodes.rja.requests.packet;

public class PingRequest extends PacketRequest {

    public PingRequest() {
        super("Ping");
        addData("data", 0);
    }

}
