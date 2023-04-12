package de.joshicodes.rja;

import com.google.gson.JsonObject;
import de.joshicodes.rja.event.EventListener;
import de.joshicodes.rja.event.IncomingEvent;
import de.joshicodes.rja.event.message.MessageReceivedEvent;
import de.joshicodes.rja.event.self.ReadyEvent;
import de.joshicodes.rja.object.enums.CachingPolicy;
import de.joshicodes.rja.requests.RequestHandler;
import de.joshicodes.rja.util.HttpUtil;

import javax.annotation.Nullable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class RJABuilder {

    private RJA build;

    public static final String DEFAULT_API_URL = "https://api.revolt.chat";

    private final Logger logger;
    private String apiUrl;
    private final JsonObject apiBase;
    private final String token;

    private List<EventListener> eventListeners;
    private List<IncomingEvent> events;

    private List<CachingPolicy> cachingPolicies;

    /**
     * Creates a new RJABuilder instance with the given token.
     * @param token The Bot token
     */
    public RJABuilder(String token) {

        logger = Logger.getLogger("RJA");
        final Handler handler = new ConsoleHandler();
        handler.setFormatter(new RJALogFormatter());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);

        this.token = token;
        apiUrl = DEFAULT_API_URL;
        apiBase = HttpUtil.readJson(apiUrl);

        enableCaching(CachingPolicy.values()); // Enable all caching policies by default

    }

    public RJABuilder setApiUrl(String apiUIrl) {
        this.apiUrl = apiUIrl;
        return this;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getWSUrl() {
        return apiBase.get("ws").getAsString() + "?version=1&format=json&token=" + token;
    }

    public RJABuilder registerEventListener(EventListener eventListener) {
        if(eventListeners == null) eventListeners = new ArrayList<>();
        eventListeners.add(eventListener);
        return this;
    }

    /**
     * Registers an incoming event.
     * Incoming Events are events that are received from the websocket.
     * These will be handled automatically.
     * You can listen to these events using {@link EventListener}
     * @param event The event to register
     * @return This builder instance
     *
     * @see EventListener
     * @see IncomingEvent
     *
     */
    public RJABuilder registerEvent(IncomingEvent event) {
        if(events == null) events = new ArrayList<>();
        events.add(event);
        return this;
    }

    /**
     * Registers incoming events.
     * Incoming Events are events that are received from the websocket.
     * These will be handled automatically.
     * You can listen to these events using {@link EventListener}
     * @param events The events to register
     * @return This builder instance
     *
     * @see EventListener
     * @see IncomingEvent
     *
     */
    public RJABuilder registerEvents(IncomingEvent... events) {
        if(this.events == null) this.events = new ArrayList<>();
        Collections.addAll(this.events, events);
        return this;
    }

    /**
     * Enables caching for the given policies.
     * It is recommended to enable caching only for the objects you need, as caches can grow quite large.
     * Caching is enabled by default for all policies.
     * @param policies The policies to enable caching for
     * @return This builder instance
     */
    public RJABuilder enableCaching(CachingPolicy... policies) {
        if(cachingPolicies == null) cachingPolicies = new ArrayList<>();
        Collections.addAll(cachingPolicies, policies);
        return this;
    }

    /**
     * Disables caching for the given policies.
     * @param policies The policies to disable caching for
     * @return This builder instance
     */
    public RJABuilder disableCaching(CachingPolicy... policies) {
        if(cachingPolicies == null) return this; // Cannot disable caching if nothing is enabled
        for(CachingPolicy policy : policies) {
            cachingPolicies.remove(policy);
        }
        return this;
    }



    /**
     * Gets the built RJA instance.
     * If the RJA instance has not been built yet, this will return null.
     * To build the RJA instance use {@link #build()}
     * @return The RJA instance or null
     */
    @Nullable
    public  RJA get() {
        return build;
    }

    /**
     * Builds the RJA instance.
     * @return The RJA instance
     * @throws URISyntaxException If the websocket url is invalid
     */
    public RJA build() throws URISyntaxException {

        if(build != null) return build;

        final Thread thread = Thread.currentThread();

        registerEvents(
                new ReadyEvent(),
                new MessageReceivedEvent()
        );

        final RequestHandler requestHandler = new RequestHandler(this, eventListeners, events);
        final RJA rja = new RJA(cachingPolicies) {

            @Override
            public Logger getLogger() {
                return logger;
            }

            @Override
            public String getApiUrl() {
                return apiUrl;
            }

            @Override
            public RequestHandler getRequestHandler() {
                return requestHandler;
            }

            @Override
            public Thread mainThread() {
                return thread;
            }

        };

        new Thread(requestHandler::connect).start();

        build = rja;

        return rja;

    }

}
