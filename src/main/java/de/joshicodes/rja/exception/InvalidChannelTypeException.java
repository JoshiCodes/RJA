package de.joshicodes.rja.exception;

import de.joshicodes.rja.object.channel.ChannelType;

public class InvalidChannelTypeException extends RuntimeException {

    public InvalidChannelTypeException(String id, ChannelType expected, ChannelType actual) {
        super(
                expected != actual ?
                        "Invalid channel type for channel with id '" + id + "'! Expected " + expected.name() + ", but Channel is: " + actual.name()
                        :
                        "Exception thrown for unknown reason. Expected Channel Type is equal to actual Channel Type."
        );
    }

}
