package de.joshicodes.rja;

import com.google.gson.JsonObject;
import de.joshicodes.rja.cache.CacheMap;
import de.joshicodes.rja.exception.InvalidChannelTypeException;
import de.joshicodes.rja.exception.RJAPingException;
import de.joshicodes.rja.object.Attachment;
import de.joshicodes.rja.object.Emoji;
import de.joshicodes.rja.object.InputFile;
import de.joshicodes.rja.object.channel.ChannelType;
import de.joshicodes.rja.object.channel.DirectChannel;
import de.joshicodes.rja.object.channel.GenericChannel;
import de.joshicodes.rja.object.channel.TextChannel;
import de.joshicodes.rja.object.enums.CachingPolicy;
import de.joshicodes.rja.object.message.Message;
import de.joshicodes.rja.object.server.Member;
import de.joshicodes.rja.object.server.Server;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.requests.RequestHandler;
import de.joshicodes.rja.requests.file.FileHandler;
import de.joshicodes.rja.requests.rest.interaction.FetchEmojiRequest;
import de.joshicodes.rja.requests.rest.channel.info.FetchChannelRequest;
import de.joshicodes.rja.requests.rest.message.FetchMessageRequest;
import de.joshicodes.rja.requests.rest.server.FetchServerRequest;
import de.joshicodes.rja.requests.rest.server.member.FetchMemberRequest;
import de.joshicodes.rja.requests.rest.user.FetchUserRequest;
import de.joshicodes.rja.requests.rest.user.OpenDirectMessageRequest;
import de.joshicodes.rja.requests.rest.user.self.FetchSelfRequest;
import de.joshicodes.rja.rest.EditSelfRestAction;
import de.joshicodes.rja.rest.RestAction;
import de.joshicodes.rja.rest.SimpleRestAction;

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

    private final List<CachingPolicy> cachingPolicies;

    private final CacheMap<String, User> userCache;
    private final CacheMap<String, Member> memberCache;
    private final CacheMap<String, Message> messageCache;
    private final CacheMap<String, Emoji> emojiCache;
    private final CacheMap<String, GenericChannel> channelCache;
    private final CacheMap<String, Server> serverCache;

    private final FileHandler fileHandler;

    RJA(RJABuilder builder, List<CachingPolicy> cachingPolicies) {

        this.cachingPolicies = cachingPolicies;

        if(cachingPolicies.contains(CachingPolicy.MEMBER)) {
            userCache = new CacheMap<>();
            memberCache = new CacheMap<>();
        } else {
            userCache = null;
            memberCache = null;
        }

        if(cachingPolicies.contains(CachingPolicy.MESSAGE)) messageCache = new CacheMap<>();
        else messageCache = null;

        if(cachingPolicies.contains(CachingPolicy.EMOJI)) emojiCache = new CacheMap<>();
        else emojiCache = null;

        if(cachingPolicies.contains(CachingPolicy.SERVER)) {
            channelCache = new CacheMap<>();
            serverCache = new CacheMap<>();
        } else {
            channelCache = null;
            serverCache = null;
        }

        fileHandler = new FileHandler(builder.getFileserverUrl(), this);

    }

    abstract public Logger getLogger();

    abstract public String getApiUrl();
    abstract public String getFileserverUrl();

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
        return new SimpleRestAction<>(this, () -> {
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
        });
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
        return new SimpleRestAction<>(this, () -> {
            if(userCache.containsKey(id)) return userCache.get(id);
            else return new RestAction<>(rja, () -> new FetchUserRequest(id)).complete();
        });
    }

    public RestAction<Member> retrieveMember(final Server server, final User user) {
        return retrieveMember(server, user.getId());
    }

    public RestAction<Member> retrieveMember(final Server server, final String id) {
        return new RestAction<>(this, () -> new FetchMemberRequest(server.getId(), id));
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
        return retrieveMessage(channel, id, false);
    }

    public RestAction<Message> retrieveMessage(String channel, String id, boolean forceFetch) {
        if(channel == null || channel.isEmpty() || id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Channel and ID cannot be null or empty");
        }
        final RJA rja = this;
        return new SimpleRestAction<>(this, () -> {
            if(messageCache.containsKey(id) && !forceFetch) return messageCache.get(id);
            else return new RestAction<>(rja, () -> new FetchMessageRequest(channel, id)).complete();
        });
    }

    public RestAction<Emoji> retrieveEmoji(String id) {
        final RJA rja = this;
        return new SimpleRestAction<>(this, () -> {
            if(emojiCache.containsKey(id)) return emojiCache.get(id);
            else return new RestAction<>(rja, () -> new FetchEmojiRequest(id)).complete();
        });
    }

    public RestAction<DirectChannel> retrieveDirectChannel(String id) {
        final RJA rja = this;
        return new SimpleRestAction<>(this, () -> {
            if(channelCache.containsKey(id) && channelCache.get(id) instanceof DirectChannel direct) return direct;
            else return new RestAction<>(rja, () -> new OpenDirectMessageRequest(id)).complete();
        });
    }

    /**
     * Retrieves a GenericChannel from the cache or from the API.
     * For specific channel types, use the corresponding methods.
     * @param id The id of the channel.
     * @return The RestAction containing the channel. Use {@link RestAction#complete()} or {@link RestAction#queue} to get the channel. Channel can be null.
     */
    public RestAction<GenericChannel> retrieveChannel(String id) {
        final RJA rja = this;
        return new SimpleRestAction<>(this, () -> {
            if(channelCache.containsKey(id)) return channelCache.get(id);
            else return new RestAction<>(rja, () -> new FetchChannelRequest(id)).complete();
        });
    }

    public TextChannel getTextChannel(String id) {
        GenericChannel c = retrieveChannel(id).complete();
        if(c instanceof TextChannel tc) return tc;
        else throw new InvalidChannelTypeException(id, ChannelType.TEXT_CHANNEL, c.getType());
    }

    public RestAction<Server> retrieveServer(String serverId) {
        final RJA rja = this;
        return new SimpleRestAction<>(this, () -> {
            if(serverCache.containsKey(serverId)) return serverCache.get(serverId);
            else return new RestAction<>(rja, () -> new FetchServerRequest(serverId)).complete();
        });
    }

    public void cacheMessage(Message message) {
        if(messageCache == null) return; // Caching is disabled
        messageCache.put(message.getId(), message);
    }

    public void cacheEmoji(Emoji emoji) {
        if(emoji == null) return;
        if(emojiCache == null) return; // Caching is disabled
        emojiCache.put(emoji.getId(), emoji);
    }

    public User cacheUser(JsonObject user) {
        User u = User.from(this, user);
        if(userCache == null) return u; // Caching is disabled
        if(u != null) {
            userCache.put(u.getId(), u);
            //getLogger().info("Loaded user " + u.getUsername()); // DEBUG
        } else {
            getLogger().warning("Failed to load user!");
        }
        return u;
    }

    public void cacheMember(Member member) {
        if(memberCache == null) return; // Caching is disabled
        if(member != null) {
            memberCache.put(member.getId(), member);
            //getLogger().info("Loaded member " + m.getDisplayName()); // DEBUG
        } else {
            getLogger().warning("Failed to load member!");
        }
    }

    public GenericChannel cacheChannel(JsonObject channel) {
        GenericChannel c = GenericChannel.from(this, channel);
        return cacheChannel(c);
    }

    public GenericChannel cacheChannel(GenericChannel channel) {
        if(channelCache == null) return channel; // Caching is disabled
        if(channel != null) {
            channelCache.remove(channel.getId());
            channelCache.put(channel.getId(), channel);
            //getLogger().info("Loaded channel " + c.getName()); // DEBUG
        } else {
            getLogger().warning("Failed to load channel!");
        }
        return channel;
    }

    public Server cacheServer(Server cachedServer) {
        if(serverCache == null) return null; // Caching is disabled
        if(cachedServer != null) {
            serverCache.remove(cachedServer.getId());
            serverCache.put(cachedServer.getId(), cachedServer);
            //getLogger().info("Loaded server " + s.getName()); // DEBUG
        } else {
            getLogger().warning("Failed to load server!");
        }
        return cachedServer;
    }

    /**
     * Retrieves the User cache.
     * @return The user cache or null if the caching policy for {@link CachingPolicy#MEMBER} is disabled.
     */
    public CacheMap<String, User> getUserCache() {
        return userCache;
    }

    /**
     * Retrieves the Message cache.
     * @return The message cache or null if the caching policy for {@link CachingPolicy#MESSAGE} is disabled.
     */
    public CacheMap<String, Message> getMessageCache() {
        return messageCache;
    }

    /**
     * Retrieves the Emoji cache.
     * @return The emoji cache or null if the caching policy for {@link CachingPolicy#EMOJI} is disabled.
     */
    public CacheMap<String, Emoji> getEmojiCache() {
        return emojiCache;
    }

    /**
     * Retrieves the Channel cache.
     * @return The channel cache or null if the caching policy for {@link CachingPolicy#SERVER} is disabled.
     */
    public CacheMap<String, GenericChannel> getChannelCache() {
        return channelCache;
    }

    public CacheMap<String, Server> getServerCache() {
        return serverCache;
    }

    public RestAction<User> retrieveSelfUser() {
        return new RestAction<>(this, FetchSelfRequest::new);
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

    public List<CachingPolicy> getCachingPolicies() {
        return cachingPolicies;
    }

}
