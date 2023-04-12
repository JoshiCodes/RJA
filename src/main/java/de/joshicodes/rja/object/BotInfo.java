package de.joshicodes.rja.object;

import com.google.gson.JsonObject;
import de.joshicodes.rja.util.JsonUtil;

import javax.annotation.Nullable;

public abstract class BotInfo {

    public static BotInfo from(JsonObject bot) {
        if(bot == null) {
            return new BotInfo() {
                @Override
                public boolean isBot() {
                    return false;
                }

                @Nullable
                @Override
                public String owner() {
                    return null;
                }
            };
        }

        final boolean isBot = true; // if the bot object is not null, the user is a bot
        final String owner = JsonUtil.getString(bot, "owner", null);

        return new BotInfo() {
            @Override
            public boolean isBot() {
                return isBot;
            }

            @Nullable
            @Override
            public String owner() {
                return owner;
            }
        };
    }

    abstract public boolean isBot();

    /**
     * Returns the owner of the bot. Is null if {@link #isBot()} is false.
     * @return The owner of the bot.
     */
    @Nullable
    abstract public String owner();

}
