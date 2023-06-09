package de.joshicodes.rja.requests.rest.server.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.server.Member;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.requests.rest.RestRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FetchAllMembersRequest extends RestRequest<HashMap<User, Member>> {

    public FetchAllMembersRequest(String serverId, boolean excludeOffline) {
        super("GET", "/servers/" + serverId + "/members");
        addData("exclude_offline", excludeOffline);
    }

    @Override
    public HashMap<User, Member> fetch(RJA rja, int statusCode, JsonElement data) {
        if(statusCode != 200) return null;
        if(!data.isJsonObject()) return null;

        JsonObject object = data.getAsJsonObject();

        List<User> users = new ArrayList<>();
        List<Member> members = new ArrayList<>();


        if (object.has("members")) {
            JsonArray membersArray = object.getAsJsonArray("members");

            for (JsonElement element : membersArray) {
                if(!element.isJsonObject()) continue;
                JsonObject memberObject = element.getAsJsonObject();
                Member member = Member.from(rja, memberObject);
                members.add(member);
                rja.cacheMember(member);
            }

        }

        if (object.has("users")) {
            JsonArray usersArray = object.getAsJsonArray("users");

            for (JsonElement element : usersArray) {
                if(!element.isJsonObject()) continue;
                JsonObject userObject = element.getAsJsonObject();
                try {
                    User user = rja.cacheUser(userObject);
                    users.add(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        HashMap<User, Member> map = new HashMap<>();
        for (User user : users) {
            members.stream().filter(m -> m.getId().equals(user.getId())).findFirst().ifPresent(found -> map.put(user, found));
        }
        return map;

    }

}
