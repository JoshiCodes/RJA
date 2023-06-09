package de.joshicodes.rja.object.server;

import java.util.List;

public interface IPermissionHolder {

    List<Permission> getAllowedPermissions();
    default boolean hasPermission(Permission perm) {
        return getAllowedPermissions().contains(perm);
    }

    List<Permission> getDeniedPermissions();

    default List<Permission> getAllPermissions() {
        List<Permission> perms = getAllowedPermissions();
        perms.addAll(getDeniedPermissions());
        return perms;
    }

}
