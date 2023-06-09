package de.joshicodes.rja.object.server;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.RJAColor;

public abstract class Role implements IPermissionHolder {

    public static Role from(final RJA rja, final String id, final JsonObject object) {
        return new RoleImpl(rja, id, object);
    }

    private final RJA rja;

    Role(RJA rja) {
        this.rja = rja;
    }

    public RJA getRJA() {
        return rja;
    }

    abstract public String getId();
    abstract public String getName();
    abstract public String getRawColor();

    abstract public boolean isHoisted();

    /**
     * Returns the position of this role.
     * Smaller values take priority over larger values.
     * @return The position of this role
     */
    abstract public int getRank();

    /**
     * Returns the color of this role.
     * <br>
     * <b>Note: The {@link RJAColor} Implementation for Regex Colors is not yet implemented.</b>
     * <br>
     * <b>This will <u>always</u> return the same result as new RJAColor(Color.WHITE) until implemented</b>
     *
     * @see RJAColor
     * @see RJAColor#RJAColor(String)
     * @return The color of this role.
     */
    public RJAColor getColor() {
        return new RJAColor(getRawColor());
    }

}
