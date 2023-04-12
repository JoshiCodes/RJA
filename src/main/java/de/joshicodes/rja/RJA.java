package de.joshicodes.rja;

import com.google.gson.JsonObject;
import de.joshicodes.rja.cache.Cache;
import de.joshicodes.rja.object.Message;
import de.joshicodes.rja.object.User;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.enums.CachingPolicy;
import de.joshicodes.rja.requests.RequestHandler;
import de.joshicodes.rja.requests.rest.FetchUserRequest;
import de.joshicodes.rja.requests.rest.channel.info.FetchChannelRequest;
import de.joshicodes.rja.requests.rest.self.FetchSelfRequest;
import de.joshicodes.rja.rest.EditSelfRestAction;
import de.joshicodes.rja.rest.RestAction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the RJA API. (Revolt Java API)
 * Is used to handle events and send requests.
 * Should be created using the {@link RJABuilder}.
 *
 * @author JoshiCodes
 *
 * @see RJABuilder
 *
 */
public abstract class RJA {

    private final Cache<User> userCache;
    private final Cache<Message> messageCache;
    private final Cache<GenericChannel> channelCache;

    RJA(List<CachingPolicy> cachingPolicies) {

        if(cachingPolicies.contains(CachingPolicy.MEMBER)) userCache = new Cache<>();
        else userCache = null;

        if(cachingPolicies.contains(CachingPolicy.MESSAGE)) messageCache = new Cache<>();
        else messageCache = null;

        if(cachingPolicies.contains(CachingPolicy.SERVER)) {
            channelCache = new Cache<>();
        } else {
            channelCache = null;
        }

    }

    abstract public Logger getLogger();

    abstract public String getApiUrl();
    abstract public RequestHandler getRequestHandler();
    abstract public Thread mainThread();

    public void shutdownNow() {
        mainThread().interrupt();
    }

    /**
     * Retrieves a user from the cache.
     * If the user is not in the cache, the user will be fetched from the API.
     * If {@link CachingPolicy#MEMBER} is disabled, the user will <b>always</b> be fetched from the API and possibly null.
     * @param id The id of the user.
     * @return The RestAction containing the user. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the user. User can be null.
     *
     * @see RestAction
     *
     */
    public RestAction<User> retrieveUser(String id) {
        final RJA rja = this;
        return new RestAction<>(this) {
            @Override
            public User complete() {
                if(userCache != null) {
                    // Caching for User is enabled
                    User u = userCache.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
                    if(u != null) return u;
                }
                // Caching is disabled or user is not found in cache
                FetchUserRequest request = new FetchUserRequest(id);
                return getRequestHandler().sendRequest(rja, request);
            }
        };
    }

    /**
     * Retrieves a message from the cache.
     * If the message is not in the cache, the message will be fetched from the API. TODO
     * @param id The id of the message.
     * @return The restaction containing the message. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the message. Message can be null.
     *
     * @see RestAction
     */
    public RestAction<Message> retrieveMessage(String channel, String id) {
        return new RestAction<>(this) {
            @Override
            public Message complete() {
                Message msg = messageCache.stream().filter(message -> message.getId().equals(id)).findFirst().orElse(null);
                if(msg != null) return msg;
                // TODO: Fetch message from API
                return null;
            }
        };
    }

    public RestAction<GenericChannel> retrieveChannel(String id) {
        final RJA rja = this;
        return new RestAction<>(this) {
            @Override
            public GenericChannel complete() {
                if(channelCache != null) {
                    // Caching for Channel is enabled
                    GenericChannel c = channelCache.stream().filter(channel -> channel.getId().equals(id)).findFirst().orElse(null);
                    if(c != null) return c;
                }
                // Caching is disabled or channel is not found in cache
                FetchChannelRequest request = new FetchChannelRequest(id);
                return getRequestHandler().sendRequest(rja, request);
            }
        };
    }

    public void cacheMessage(Message message) {
        if(messageCache == null) return;
        messageCache.add(message);
    }

    public void cacheUser(JsonObject user) {
        if(userCache == null) return;
        User u = User.from(this, user);
        if(u != null) {
            userCache.add(u);
            //getLogger().info("Loaded user " + u.getUsername()); // DEBUG
        } else {
            getLogger().warning("Failed to load user!");
        }
    }

    public GenericChannel cacheChannel(JsonObject channel) {
        GenericChannel c = GenericChannel.from(this, channel);
        if(channelCache == null) return c;
        if(c != null) {
            channelCache.add(c);
            //getLogger().info("Loaded channel " + c.getName()); // DEBUG
        } else {
            getLogger().warning("Failed to load channel!");
        }
        return c;
    }

    /**
     * Retrieves the User cache.
     * @return The user cache or null if the caching policy for {@link CachingPolicy#MEMBER} is disabled.
     */
    public Cache<User> getUserCache() {
        return userCache;
    }

    /**
     * Retrieves the Message cache.
     * @return The message cache or null if the caching policy for {@link CachingPolicy#MESSAGE} is disabled.
     */
    public Cache<Message> getMessageCache() {
        return messageCache;
    }

    /**
     * Retrieves the Channel cache.
     * @return The channel cache or null if the caching policy for {@link CachingPolicy#SERVER} is disabled.
     */
    public Cache<GenericChannel> getChannelCache() {
        return channelCache;
    }

    public RestAction<User> retrieveSelfUser() {
        return new RestAction<>(this) {
            @Override
            public User complete() {
                FetchSelfRequest request = new FetchSelfRequest();
                return getRequestHandler().sendRequest(RJA.this, request);
            }
        };
    }

    public EditSelfRestAction editSelfUser() {
        return new EditSelfRestAction(this);
    }

}
