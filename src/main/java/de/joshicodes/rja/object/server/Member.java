package de.joshicodes.rja.object.server;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.IMentionable;
import de.joshicodes.rja.object.user.Avatar;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.rest.RestAction;

import java.util.List;

public abstract class Member implements IMentionable, IPermissionHolder {

    public static Member from(RJA rja, JsonObject data) {
        return new MemberImpl(rja, data);
    }

    abstract public RJA getRJA();

    abstract public String getId();

    public RestAction<User> retrieveUser() {
        return getRJA().retrieveUser(getId());
    }

    abstract public String getServerId();

    public RestAction<Server> retrieveServer() {
        return getRJA().retrieveServer(getServerId());
    }

    abstract public String joinedAtRaw();
    abstract public String getNickname();

    abstract public Avatar getAvatar();

    abstract public List<Role> getRoles();

    abstract public String timeoutRaw();

    public long joinedAt() {
        if(joinedAtRaw() == null) return -1;
        // joinedAtRaw() returns a ISO8601 formatted timestamp
        return java.time.Instant.parse(joinedAtRaw()).getEpochSecond();
    }

    public boolean hasNickname() {
        return getNickname() != null;
    }

    public long timeout() {
        if(timeoutRaw() == null) return -1;
        // timeoutRaw() returns a ISO8601 formatted timestamp
        return java.time.Instant.parse(timeoutRaw()).getEpochSecond();
    }

    @Override
    public String getAsMention() {
        return "<@" + getId() + ">";
    }

}
