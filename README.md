# RJA (Revolt Java API)
### RJA is a Java API for the Revolt chat platform, mostly focused on bots.
![Github Release](https://img.shields.io/github/v/release/JoshiCodes/RJA?include_prereleases)

### Note: This is a work in progress, and is not yet ready for use.


## Install

You can download the latest version from the [releases page](https://github.com/JoshiCodes/RJA/releases/latest)

Replace `VERSION HERE` with the latest version.

### Maven
```xml
<dependency>
    <groupId>de.joshicodes</groupId>
    <artifactId>rja</artifactId>
    <version>[VERSION HERE]</version>
</dependency>
```


## Usage
To create a new `RJA` Instance, you can use the `RJABuilder`.

```java
RJABuilder builder = new RJABuilder("token"); // Replace "token" with your bot token
// do some stuff
RJA rja = builder.build();
```
This creates a new RJA Instance, which you can use to do stuff as your bot.

You can also modify some things before building the instance:
```java
RJABuilder builder = ...
        
builder.setStatus("Test Status", UserStatus.Presence.FOCUS); // Sets the Status of the Bot to "Test Status" with "FOCUS" as presence type.
builder.doCleanStatus(true); // Resets the status of the Bot if it does not get changed at startup. It is default true. If false, the status of the bot stays as before.
        
builder.registerEventListener(new MyEventListener()); // Registers a new EventListener. More about EventListeners at #EventListeners or in the JavaDocs
        
builder.disableCaching(CachingPolicy.MEMBER); // Disables caching for the Member Cache. Every CachePolicy is enabled by default.
// More about Caching at #Caching or in the JavaDocs
```

But wait, there is more. After building your `RJA`` instance you can request stuff from the API.
```java
RJA rja = ...

User user = rja.retrieveUser("01GXTJK9Q1JZVR1NZ32CGDCDKN").complete();  // Retrieves a User by its ID.

Message message = rja.retrieveMessage("channelId", "messageId").complete(); // Retrieves a Message by the channel id and the message id.
   
```

### RestActions
These methods usually make use of the `RestAction` class. RestActions are used to fetch and handle data async.
To get the data, you can use the `complete()` method, which *can* block the current thread until the data is fetched.
If you want to handle the data async, you can use the `queue()` method, which takes a `Consumer<T>` as parameter.
The queue() method can also be called without any parameters or with two parameters, which are a `Consumer<T>` and a `Consumer<Throwable>`.

```java
RJA rja = builder.build();

// One Consumer, only for the user
rja.retrieveUser("userId").queue(user -> {
    // do stuff with the user
});

// Two Consumers, one for the user and one for the exception
rja.retrieveUser("userId").queue(user -> {
    // do stuff with the user
}, throwable -> {
    // handle the exception
});

// Get the user sync
User user = rja.retrieveUser("userId").complete();
```

### Sending Messages
You can send Messages to every `GenericChannel` using `GenericChannel#sendMessage` or `GenericChannel#sendEmbeds`.
Both of this Methods return a `MessageAction` which can be used to modify the message before sending it.
<br>
To send a private Message to a User, you can use `User#openPrivateChannel` to retrieve a RestAction with the DirectChannel of the User.
To send messages to this `DirectChannel`, you can use the same methods as with `GenericChannel`.

### EventListeners
EventListeners are used to handle events. You can register them with the `RJABuilder#registerEventListener()` method.
```java
RJABuilder builder = new RJABuilder("token");
builder.registerEventListener(new MyEventListener());
```

Your EventListener class must implement the `EventListener` interface.
To listen to a specific event, you can use the `@EventHandler` annotation on a method, this method requires the Event you want to listen to as parameter.
The Name of the method does not matter, but it is recommended to use the name of the event.

MyEventListener.java:
```java
import de.joshicodes.rja.event.EventHandler;

public class MyEventListener implements EventListener {

    @EventHandler
    public void onReady(ReadyEvent event) {
        System.out.println("Bot is ready!");
    }
    
    @EventHandler
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Received message: " + event.getMessage().getContent());
    }
    
}
```

### Caching
RJA has a built-in caching system, which can be disabled for specific caches.
By default, all caches are enabled.
You can disable them with the `RJABuilder#disableCaching(CachingPolicy...)` method.
If you disable a cache, you can still retrieve the data from the API, but it will not be cached and may result in a longer response time or rate limiting.
