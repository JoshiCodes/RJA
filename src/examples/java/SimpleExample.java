import de.joshicodes.rja.RJA;
import de.joshicodes.rja.RJABuilder;
import de.joshicodes.rja.event.EventHandler;
import de.joshicodes.rja.event.EventListener;
import de.joshicodes.rja.event.self.ReadyEvent;
import de.joshicodes.rja.object.enums.CachingPolicy;
import de.joshicodes.rja.object.user.UserStatus;

import java.net.URISyntaxException;

public class SimpleExample {

    public static void main(String[] args) throws URISyntaxException {

        // Read the token from the environment variable
        final String token = System.getenv("REVOLT_TOKEN");

        // Create a new RJABuilder
        RJABuilder builder = new RJABuilder(token); // You can also use RJABuilder#createWithoutCaching(String) to disable caching for all policies.

        // If you want to use a local instance of the Revolt API, you can set the API URL with:
        // builder.setApiUrl("localhost:3000");

        // Set the status of the bot
        builder.setStatus(
                "Revolt Java API",    // The status message
                UserStatus.Presence.ONLINE  // You can also use UserStatus.Presence.OFFLINE, UserStatus.Presence.IDLE, UserStatus.Presence.BUSY, UserStatus.Presence.FOCUS
        );

        // Disable caching for selected policies. By default, all policies are enabled.
        // You can also create a new RJABuilder with RJABuilder#createWithoutCaching(String) to disable caching for all policies.
        // You can also use RJABuilder#enableCaching(CachingPolicy...) to enable caching for selected policies, if previously disabled.
        builder.disableCaching(
                CachingPolicy.MEMBER, // Disables caching for users
                CachingPolicy.MESSAGE // Disables caching for messages
        );

        // Register an event listener
        builder.registerEventListener(
                new EventListener() {
                    @EventHandler
                    public void onReady(ReadyEvent event) {
                        event.getLogger().info("Ready from Event! Username: " + event.getSelf().getUsername());
                    }
                }
        );

        // Build the RJA instance
        RJA rja = builder.build();

        // Don't do anything with the RJA instance, as builder.build() is non-blocking and RJA may not be ready yet.
        rja.awaitReady(); // Wait for RJA to be ready, blocking the current thread until RJA is ready. This can block forever, if RJA can't connect to the API due to whatever reason.
        // You can also use rja.awaitReady(long, TimeUnit) to wait for a specific amount of time.
        // Or check if RJA is ready with rja.isReady().

        rja.getLogger().info("Ready! Username: " + rja.retrieveSelfUser().complete().getUsername());

    }

}
