package de.joshicodes.rja.object.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class RoleImpl extends Role {

    private final String id;

    private String name;

    private boolean hoisted;
    private int rank;

    private List<Permission> allowedPerms;
    private List<Permission> deniedPerms;

    private String rawColor;

    public RoleImpl(final RJA rja, final String id, final JsonObject object) {
        super(rja);

        System.out.println(object);

        this.id = id;

        name = JsonUtil.getString(object, "name", null);

        hoisted = JsonUtil.getBoolean(object, "hoist", false);
        rank = JsonUtil.getInt(object, "rank", 0);

        this.allowedPerms = new ArrayList<>();
        this.deniedPerms = new ArrayList<>();
        if(object.has("permissions")) {
          JsonObject permsObject = object.getAsJsonObject("permissions");
          long allowed = -1;
          long denied = -1;
          if(permsObject.has("a")) {
              allowed = permsObject.get("a").getAsLong();
          }
          if(permsObject.has("d")) {
              denied = permsObject.get("a").getAsLong();
          }
          updatePermissions(allowed, denied);
        }

        rawColor = JsonUtil.getString(object, "colour", null);

    }

    void updatePermissions(long allowed, long denied) {
        if(allowed != -1) {
            for(Permission perm : Permission.values()) {
                if(Permission.contains(allowed, perm)) {
                    allowedPerms.add(perm);
                }
            }
        }
        if(denied != -1) {
            for(Permission perm : Permission.values()) {
                if(Permission.contains(denied, perm)) {
                    deniedPerms.add(perm);
                }
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRawColor() {
        return rawColor;
    }

    @Override
    public boolean isHoisted() {
        return hoisted;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public List<Permission> getAllowedPermissions() {
        return allowedPerms;
    }

    @Override
    public List<Permission> getDeniedPermissions() {
        return deniedPerms;
    }

}
