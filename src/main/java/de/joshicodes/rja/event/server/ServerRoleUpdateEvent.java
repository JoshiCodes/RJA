package de.joshicodes.rja.event.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.object.server.Role;
import de.joshicodes.rja.object.server.RoleImpl;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.requests.rest.RestResponse;
import de.joshicodes.rja.requests.rest.server.FetchServerRequest;

public class ServerRoleUpdateEvent extends IncomingEvent {

    public ServerRoleUpdateEvent() {
        this(null, null, null);
    }

    private final String serverId;
    private final String roleId;

    public ServerRoleUpdateEvent(RJA rja, String serverId, String roleId) {
        super(rja, "ServerRoleUpdate");
        this.serverId = serverId;
        this.roleId = roleId;
    }

    public Role getRole() {
        return getRJA().retrieveServer(serverId, true).complete().getRole(roleId);
    }

    public String getServerId() {
        return serverId;
    }

    public String getRoleId() {
        return roleId;
    }

    @Override
    public IncomingEvent handle(RJA rja, JsonObject object) {

        String id = object.get("id").getAsString();
        boolean inCache = rja.getServerCache().containsKey(id);
        if(!inCache) {
            // Server not in cache, cannot update with partial data -> fetch full server
            FetchServerRequest request = new FetchServerRequest(id);
            RestResponse<Server> response = rja.getRequestHandler().fetchRequest(rja, request);
            if(response.isOk()) {
                Server server = rja.cacheServer(response.object());
                return new ServerUpdateEvent(rja, server);
            }
        }
        String roleId = object.get("role_id").getAsString();

        Server server = rja.getServerCache().get(id);
        if(server != null) {
            if(server.getRole(roleId) instanceof RoleImpl impl) {  // Always true
                impl.update(object.getAsJsonObject("data"));
                server.updateRole(impl);
            }
            if(object.has("clear")) {
                JsonArray clear = object.get("clear").getAsJsonArray();
                if(!clear.isEmpty()) {
                    // can only contain "Colour"
                    clear.forEach(element -> {
                        String name = element.getAsString();
                        switch(name) {
                            case "Colour":
                                // TODO
                                break;
                            default:
                                break;
                        }
                    });
                }
            }

           // DEBUG server.getRoles().forEach(role -> System.out.println(role.getAllowedPermissions()));
         }

        return new ServerUpdateEvent(rja, rja.cacheServer(server));

    }

}
