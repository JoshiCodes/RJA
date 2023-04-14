package de.joshicodes.rja.object.message.embed;

import de.joshicodes.rja.object.Attachment;
import de.joshicodes.rja.object.RJAColor;

import java.awt.*;

public class EmbedBuilder {

    private String title;
    private String description;
    private RJAColor color;

    private String iconUrl;
    private String url;

    private String media;

    public EmbedBuilder() {}

    /**
     * Sets the title of the embed.
     * @param title The title of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the title and url of the embed.
     * @param title The title of the embed.
     * @param url The url of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setTitle(String title, String url) {
        this.title = title;
        this.url = url;
        return this;
    }

    /**
     * Sets the description of the embed.
     * @param description The description of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the icon url of the embed.
     * @param iconUrl The icon url of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    /**
     * Sets the url of the embed.
     * @param url The url of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets the media of the embed.
     * @param media The media of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setMedia(Attachment media) {
        this.media = media.getId();
        return this;
    }

    /**
     * Sets the color of the embed.
     * @param color The color of the embed.
     * @return This EmbedBuilder instance.
     */
    public EmbedBuilder setColor(Color color) {
        this.color = new RJAColor(color);
        return this;
    }

    /**
     * Sets the color of the embed.
     * @param color The EmbedColor of the embed.
     * @return This EmbedBuilder instance.
     *
     * @see RJAColor
     */
    public EmbedBuilder setColor(RJAColor color) {
        this.color = color;
        return this;
    }

    /**
     * Builds the embed.
     * @return The built embed.
     * @throws IllegalArgumentException If any of the embed's fields are too long or invalid.
     */
    public MessageEmbed build() {

        if(title != null) {
            if(title.length() > 100) {
                throw new IllegalArgumentException("Title cannot be longer than 100 characters.");
            }
        }
        if(description != null) {
            if(description.length() > 2000) {
                throw new IllegalArgumentException("Description cannot be longer than 2000 characters.");
            }
        }
        if(iconUrl != null) {
            if(iconUrl.length() > 128) {
                throw new IllegalArgumentException("Icon url cannot be longer than 128 characters.");
            }
            if(!iconUrl.startsWith("http")) {
                throw new IllegalArgumentException("The icon url must be valid.");
            }
        }
        if(url != null) {
            if(!url.startsWith("http")) {
                throw new IllegalArgumentException("The URL must be valid.");
            }
        }

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

}
