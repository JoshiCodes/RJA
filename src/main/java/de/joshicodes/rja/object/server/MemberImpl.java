package de.joshicodes.rja.object.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.Avatar;
import de.joshicodes.rja.util.JsonUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MemberImpl extends Member {

    private final RJA rja;

    private final String id;
    private final String serverId;

    private final String joinedAtRaw;

    private String nickname;
    private Avatar avatar;
    private List<Role> roles;

    private String timeoutRaw;

    public MemberImpl(RJA rja, JsonObject data) {
        super();
        this.rja = rja;
        JsonObject idObject = data.getAsJsonObject("_id");
        this.id = idObject.get("user").getAsString();
        this.serverId = idObject.get("server").getAsString();

        this.joinedAtRaw = JsonUtil.getString(data, "joined_at", null);
        this.nickname = JsonUtil.getString(data, "nickname", null);
        this.avatar = Avatar.from(rja, JsonUtil.getObject(data, "avatar", null));

        this.roles = new ArrayList<>();
        List<String> roleIds = new ArrayList<>();
        JsonArray rolesArray = JsonUtil.getArray(data, "roles", null);
        if(rolesArray != null) {
            for(int i = 0; i < rolesArray.size(); i++) {
                roleIds.add(rolesArray.get(i).getAsString());
            }
        }
        updateRoles(roleIds);

        this.timeoutRaw = JsonUtil.getString(data, "timeout", null);

    }

    void updateRoles(@Nullable List<String> roleIds) {
        Server server = retrieveServer().complete();
        if(roleIds != null) {
            roles.clear();
            // Fetch Roles
            for(String roleId : roleIds) {
                Role role = server.getRole(roleId);
                if(role != null) {
                    roles.add(role);
                }
            }
        }
    }

    @Override
    public RJA getRJA() {
        return rja;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    @Override
    public String joinedAtRaw() {
        return joinedAtRaw;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public String timeoutRaw() {
        return timeoutRaw;
    }

}
