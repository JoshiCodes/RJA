package de.joshicodes.rja.event.self;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.server.Member;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.rest.RestAction;

import java.util.ArrayList;
import java.util.List;

/**
 * This event is fired when the bot is ready.
 * The Bot is ready when it is connected to the websocket and receives the "Authenticated" event.
 * This typically happens on startup, but can also happen when the bot reconnects to the websocket.
 */
public class ReadyEvent extends IncomingEvent {

    public ReadyEvent() {
        this(null, null, null, null, null);
    }

    private final List<User> users;
    private final List<Server> servers;
    private final List<GenericChannel> channels;
    private final List<Member> members;

    public ReadyEvent(RJA rja, List<User> users, final List<Server> servers, final List<GenericChannel> channels, final List<Member> members) {
        super(rja, "Ready");
        this.users = users;
        this.servers = servers;
        this.channels = channels;
        this.members = members;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<GenericChannel> getChannels() {
        return channels;
    }

    public List<Member> getMembers() {
        return members;
    }

    public User getSelf() {
        return retrieveSelf().complete();
    }

    public RestAction<User> retrieveSelf() {
        return getRJA().retrieveSelfUser();
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {

        List<User> userList = new ArrayList<>();
        List<Server> serverList = new ArrayList<>();
        List<GenericChannel> channelList = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();

        JsonArray users = object.get("users").getAsJsonArray();
        for(JsonElement user : users) {
            if(!user.isJsonObject()) continue;
            userList.add(rja.cacheUser(user.getAsJsonObject()));
        }
        JsonArray servers = object.get("servers").getAsJsonArray();
        for(JsonElement server : servers) {
            if(!server.isJsonObject()) continue;
            Server s = rja.cacheServer(Server.from(rja, server.getAsJsonObject()));
            serverList.add(s);
        }
        JsonArray channels = object.get("channels").getAsJsonArray();
        for(JsonElement channel : channels) {
            if(!channel.isJsonObject()) continue;
            GenericChannel c = rja.cacheChannel(channel.getAsJsonObject());
            channelList.add(c);
        }

        JsonArray members = object.get("members").getAsJsonArray();
        for(JsonElement member : members) {
            if(!member.isJsonObject()) continue;
            Member m = Member.from(rja, member.getAsJsonObject());
            rja.cacheMember(m);
            memberList.add(m);
        }

        if(object.has("emojis")) {
            JsonArray emojis = object.get("emojis").getAsJsonArray();
            for(JsonElement emoji : emojis) {
                if(!emoji.isJsonObject()) continue;
                rja.cacheEmoji(Emoji.from(rja, emoji.getAsJsonObject()));
            }
        }

        return new ReadyEvent(rja, userList, serverList, channelList, memberList);
    }

}
