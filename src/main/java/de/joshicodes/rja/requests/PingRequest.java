package de.joshicodes.rja.requests;

import de.joshicodes.rja.requests.response.ResponseData;

public class PingRequest extends Request {

    PingRequest() {
        super("Ping");
        addData("data", 0);
    }

}
