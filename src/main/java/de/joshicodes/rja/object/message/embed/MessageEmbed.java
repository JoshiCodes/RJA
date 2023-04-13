package de.joshicodes.rja.object.message.embed;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.RJAColor;
import de.joshicodes.rja.util.JsonUtil;

/**
 * MessageEmbeds represent a "Text" embed.
 */
public abstract class MessageEmbed {

    public static MessageEmbed from(RJA rja, JsonObject asJsonObject) {
        final String type = JsonUtil.getString(asJsonObject, "type", null);
        if(type == null) {
            return null;
        }
        if(!type.equals("Text")) return null;
        final String iconUrl = JsonUtil.getString(asJsonObject, "icon_url", null);
        final String url = JsonUtil.getString(asJsonObject, "url", null);
        final String title = JsonUtil.getString(asJsonObject, "title", null);
        final String description = JsonUtil.getString(asJsonObject, "description", null);
        final String media = JsonUtil.getString(asJsonObject, "media", null);
        final RJAColor color = null; // TODO

        return new MessageEmbed() {
            @Override
            public String getIconUrl() {
                return iconUrl;
            }

            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getMedia() {
                return media;
            }

            @Override
            public RJAColor getColor() {
                return color;
            }
        };

    }

    abstract public String getIconUrl();
    abstract public String getUrl();
    abstract public String getTitle();
    abstract public String getDescription();
    abstract public String getMedia();

    abstract public RJAColor getColor(); // Note: API names this "colour" but I'm using American English.

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        if(getIconUrl() != null) object.addProperty("icon_url", getIconUrl());
        if(getUrl() != null) object.addProperty("url", getUrl());
        if(getTitle() != null) object.addProperty("title", getTitle());
        if(getDescription() != null) object.addProperty("description", getDescription());
        if(getMedia() != null) object.addProperty("media", getMedia());
        if(getColor() != null) object.addProperty("colour", getColor().toString());
        return object;
    }

}
