package de.joshicodes.rja;

import com.google.gson.JsonObject;
import de.joshicodes.rja.cache.Cache;
import de.joshicodes.rja.exception.InvalidChannelTypeException;
import de.joshicodes.rja.exception.RJAPingException;
import de.joshicodes.rja.object.Attachment;
import de.joshicodes.rja.object.InputFile;
import de.joshicodes.rja.object.channel.ChannelType;
import de.joshicodes.rja.object.channel.DirectChannel;
import de.joshicodes.rja.object.channel.TextChannel;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.enums.CachingPolicy;
import de.joshicodes.rja.requests.RequestHandler;
import de.joshicodes.rja.requests.file.FileHandler;
import de.joshicodes.rja.requests.rest.message.FetchMessageRequest;
import de.joshicodes.rja.requests.rest.user.FetchUserRequest;
import de.joshicodes.rja.requests.rest.channel.info.FetchChannelRequest;
import de.joshicodes.rja.requests.rest.user.OpenDirectMessageRequest;
import de.joshicodes.rja.requests.rest.user.self.FetchSelfRequest;
import de.joshicodes.rja.rest.EditSelfRestAction;
import de.joshicodes.rja.rest.RestAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Represents the RJA API. (Revolt Java API)
 * Is used to handle events and send requests.
 * Should be created using the {@link RJABuilder}.
 *
 * @see RJABuilder
 *
 */
public abstract class RJA {

    private final Cache<User> userCache;
    private final Cache<Message> messageCache;
    private final Cache<GenericChannel> channelCache;

    private final FileHandler fileHandler;

    RJA(RJABuilder builder, List<CachingPolicy> cachingPolicies) {

        if(cachingPolicies.contains(CachingPolicy.MEMBER)) userCache = new Cache<>();
        else userCache = null;

        if(cachingPolicies.contains(CachingPolicy.MESSAGE)) messageCache = new Cache<>();
        else messageCache = null;

        if(cachingPolicies.contains(CachingPolicy.SERVER)) {
            channelCache = new Cache<>();
        } else {
            channelCache = null;
        }

        fileHandler = new FileHandler(builder.getFileserverUrl(), this);

    }

    abstract public Logger getLogger();

    abstract public String getApiUrl();
    abstract public RequestHandler getRequestHandler();
    abstract public Thread mainThread();

    abstract public boolean isReady();

    /**
     * Pings the Rest API, as specified in {@link RJABuilder#setApiUrl(String)}
     * Uses {@link #getPing(int)} with a timeout of 15 seconds.
     * @return The RestAction containing the ping in milliseconds. Use {@link RestAction#complete()} or {@link RestAction#queue} to execute the ping request and receive the ping.
     */
    public RestAction<Long> getPing() {
        return getPing(15000);  // 15000 = 15 seconds, default timeout
    }

    /**
     * Pings the Rest API, as specified in {@link RJABuilder#setApiUrl(String)}
     * @param timeout The timeout in milliseconds
     * @return The RestAction containing the ping in milliseconds. Use {@link RestAction#complete()} or {@link RestAction#queue} to execute the ping request and receive the ping.
     */
    public RestAction<Long> getPing(final int timeout) {
        return new RestAction<>(this) {
            @Override
            protected Long execute() {
                long start = System.currentTimeMillis();
                try {
                    InetAddress[] addresses = InetAddress.getAllByName(getApiUrl().replaceAll("https://", "").replaceAll("http://", ""));
                    for (InetAddress inetAddress : addresses) {
                        if (inetAddress.isReachable(timeout)) {
                            return System.currentTimeMillis() - start;
                        }
                    }
                } catch (IOException e) {
                    throw new RJAPingException("Cannot Ping Revolt REST-API", e);
                }
                throw new RJAPingException("Cannot Ping Revolt REST-API");
            }
        };
    }

    public Attachment uploadFile(File file) {
        try {
            return fileHandler.uploadFile(InputFile.of(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Attachment uploadFile(InputFile file) {
        try {
            return fileHandler.uploadFile(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
            public User execute() {
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
     * Retrieves a message from the cache or the API.
     * If the message is not in the cache, the message will be fetched from the API.
     * @param id The id of the message.
     * @return The restaction containing the message. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the message. Message can be null.
     *
     * @see RestAction
     */
    public RestAction<Message> retrieveMessage(String channel, String id) {
        return new RestAction<>(this) {
            @Override
            public Message execute() {
                if(messageCache != null) {
                    Message msg = messageCache.stream().filter(message -> message.getId().equals(id)).findFirst().orElse(null);
                    if(msg != null) return msg;
                }
                // Message is not in cache or caching is disabled
                FetchMessageRequest request = new FetchMessageRequest(channel, id);
                return getRequestHandler().sendRequest(RJA.this, request);
            }
        };
    }

    public RestAction<DirectChannel> retrieveDirectChannel(String id) {
        final RJA rja = this;
        return new RestAction<>(this) {
            @Override
            public DirectChannel execute() {
                if(channelCache != null) {
                    // Caching for Channel is enabled
                    GenericChannel c = channelCache.stream().filter(channel -> channel.getId().equals(id)).findFirst().orElse(null);
                    if(c != null) {
                        if(c instanceof DirectChannel dc) return dc;
                        else throw new InvalidChannelTypeException(id, ChannelType.DIRECT_MESSAGE, c.getType());
                    }
                }
                // Caching is disabled or channel is not found in cache
                OpenDirectMessageRequest request = new OpenDirectMessageRequest(id);
                return getRequestHandler().sendRequest(rja, request);
            }
        };
    }

    /**
     * Retrieves a GenericChannel from the cache or from the API.
     * For specific channel types, use the corresponding methods.
     * @param id The id of the channel.
     * @return The RestAction containing the channel. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the channel. Channel can be null.
     */
    public RestAction<GenericChannel> retrieveChannel(String id) {
        final RJA rja = this;
        return new RestAction<>(this) {
            @Override
            public GenericChannel execute() {
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

    public RestAction<TextChannel> retrieveTextChannel(String id) {
        return new RestAction<>(this) {
            @Override
            public TextChannel execute() {
                GenericChannel c = retrieveChannel(id).complete();
                if(c instanceof TextChannel tc) return tc;
                return null;
            }
        };
    }

    public void cacheMessage(Message message) {
        if(messageCache == null) return; // Caching is disabled
        if(messageCache.stream().anyMatch(msg -> msg.getId().equals(message.getId()))) {
            messageCache.stream().filter(msg -> msg.getId().equals(message.getId())).findFirst().ifPresent(messageCache::remove); // Message is cached, remove old one
        }
        messageCache.add(message);
    }

    public void cacheUser(JsonObject user) {
        if(userCache == null) return; // Caching is disabled
        User u = User.from(this, user);
        if(u != null) {
            if(userCache.stream().anyMatch(usr -> usr.getId().equals(u.getId()))) {
                userCache.stream().filter(usr -> usr.getId().equals(u.getId())).findFirst().ifPresent(userCache::remove); // User is cached, remove old one
            }
            userCache.add(u);
            //getLogger().info("Loaded user " + u.getUsername()); // DEBUG
        } else {
            getLogger().warning("Failed to load user!");
        }
    }

    public GenericChannel cacheChannel(JsonObject channel) {
        GenericChannel c = GenericChannel.from(this, channel);
        if(channelCache == null) return c; // Caching is disabled
        if(c != null) {
            if(channelCache.stream().anyMatch(ch -> ch.getId().equals(c.getId()))) {
                channelCache.stream().filter(ch -> ch.getId().equals(c.getId())).findFirst().ifPresent(channelCache::remove); // Channel is cached, remove old one
            }
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
            public User execute() {
                FetchSelfRequest request = new FetchSelfRequest();
                return getRequestHandler().sendRequest(RJA.this, request);
            }
        };
    }

    public EditSelfRestAction editSelfUser() {
        return new EditSelfRestAction(this);
    }

    /**
     * Blocks the current thread until the bot is ready and connected to the Revolt API.
     * This can lead to deadlocks if the bot is unable to connect to the API.
     * Resumes if Instance is ready.
     */
    public void awaitReady() {
        while(!isReady()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Blocks the current thread until the bot is ready and connected to the Revolt API.
     * Resumes if Instance is ready.
     * If the timeout is reached and the Instance is not yet ready, a {@link TimeoutException} is thrown.
     * @param timeout The timeout in the specified time unit.
     * @param unit The time unit of the timeout.
     *
     * @throws TimeoutException If the timeout is reached and the Instance is not yet ready.
     */
    public void awaitReady(long timeout, TimeUnit unit) throws TimeoutException {
        long start = System.currentTimeMillis();
        while(!isReady()) {
            if(System.currentTimeMillis() - start > unit.toMillis(timeout)) {
                throw new TimeoutException("Timeout while waiting for ready state!");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
