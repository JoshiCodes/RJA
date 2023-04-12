package de.joshicodes.rja;

import com.google.gson.JsonObject;
import de.joshicodes.rja.cache.Cache;
import de.joshicodes.rja.exception.DisabledCacheException;
import de.joshicodes.rja.object.Message;
import de.joshicodes.rja.object.User;
import de.joshicodes.rja.object.enums.CachingPolicy;
import de.joshicodes.rja.requests.RequestHandler;
import de.joshicodes.rja.rest.RestAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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

    //private final Cache<User> userCache;  // Not used, since temporary storage is not possible yet.
    private final List<User> userCache;
    private final List<Message> messageCache;

    RJA(List<CachingPolicy> cachingPolicies) {

        if(cachingPolicies.contains(CachingPolicy.MEMBER)) userCache = new ArrayList<>();
        else userCache = null;

        if(cachingPolicies.contains(CachingPolicy.MESSAGE)) messageCache = new ArrayList<>();
        else messageCache = null;

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
     * If the user is not in the cache, an empty user instance will be created. This empty user will only contain the id.
     * @param id The id of the user.
     * @return The restaction containing the user. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the user. User can be null.
     *
     * @throws DisabledCacheException If the caching policy for {@link CachingPolicy#MEMBER} is disabled.
     *
     * @see User#empty(RJA, String)
     * @see RestAction
     *
     */
    public RestAction<User> retrieveUser(String id) {
        if(userCache == null) throw new DisabledCacheException(CachingPolicy.MEMBER);
        return new RestAction<>(this) {
            @Override
            public User complete() {
                User u = userCache.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
                if(u == null) {
                    return User.empty(RJA.this, id);
                }
                return u;
            }
        };
    }

    /**
     * Retrieves a message from the cache.
     * If the message is not in the cache, this method will return null.
     * @param id The id of the message.
     * @return The restaction containing the message. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the message. Message can be null.
     *
     * @throws DisabledCacheException If the caching policy for {@link CachingPolicy#MESSAGE} is disabled.
     *
     * @see RestAction
     */
    public RestAction<Message> retrieveMessage(String id) {
        if(messageCache == null) throw new DisabledCacheException(CachingPolicy.MESSAGE);
        return new RestAction<>(this) {
            @Override
            public Message complete() {
                return messageCache.stream().filter(message -> message.getId().equals(id)).findFirst().orElse(null);
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
            getLogger().info("Loaded user " + u.getUsername());
        } else {
            getLogger().warning("Failed to load user!");
        }
    }

    /**
     * Retrieves the User cache.
     * @return The user cache or null if the caching policy for {@link CachingPolicy#MEMBER} is disabled.
     */
    public List<User> getUserCache() {
        return userCache;
    }

}
