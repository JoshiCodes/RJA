package de.joshicodes.rja.object.enums;

public enum RelationShip {
    NONE,
    USER,
    FRIEND,
    OUTGOING,
    INCOMING,
    BLOCKED,
    BLOCKED_OTHER("BlockedOther");

    private final String friendlyName;

    RelationShip() {
        this.friendlyName = name().charAt(0) + name().substring(1).toLowerCase();
    }

    RelationShip(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

}
