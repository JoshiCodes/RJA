package de.joshicodes.rja.rest;

import de.joshicodes.rja.RJA;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class RestAction<T> {

    private final boolean canMultiple;
    private boolean executed = false;

    private final RJA rja;

    public RestAction(RJA rja) {
        this(rja, false);
    }

    public RestAction(RJA rja, boolean canMultiple) {
        this.rja = rja;
        this.canMultiple = canMultiple;
    }

    public RJA getRJA() {
        return rja;
    }

    protected abstract T execute();

    /**
     * Executes the action and returns the result.
     * <b>This Method can block your current thread.</b>
     * @return The result of the action.
     */
    public T complete() {
        if (!canMultiple && executed) {
            throw new IllegalStateException("This action can only be executed once.");
        }
        this.executed = true;
        return execute();
    }

    /**
     * Queues the action to be executed in a new thread.
     * If you need to handle the result or exception, use {@link #queue(Consumer)} or {@link #queue(Consumer, Consumer)}.
     *
     * @see #queue(Consumer)
     * @see #queue(Consumer, Consumer)
     * @see #complete()
     */
    public void queue() {
        queue(null);
    }

    /**
     * Queues the action to be executed in a new thread.
     * If finished, the success consumer will be called.
     * @param success The success consumer, can be null. If you do not want to handle the result, use {@link #queue()}.
     *
     * @see #queue()
     * @see #queue(Consumer, Consumer)
     * @see #complete()
     */
    public void queue(@Nullable Consumer<T> success) {
        queue(success, null);
    }

    /**
     * Queues the action to be executed in a new thread.
     * If finished, the success consumer will be called.
     * If an exception occurs, the failure consumer will be called.
     * @param success The success consumer, can be null. If you do not want to handle the result, use {@link #queue()}.
     * @param failure The failure consumer, can be null. If you do not want to handle the exception, use {@link #queue()} or {@link #queue(Consumer)}.
     *
     * @see #queue()
     * @see #queue(Consumer)
     * @see #complete()
     */
    public void queue(@Nullable Consumer<T> success, @Nullable Consumer<Throwable> failure) {
        new Thread(() -> {
            try {
                T t = complete();
                if (success != null) {
                    success.accept(t);
                }
            } catch (Exception exception) {
                if (failure != null) {
                    failure.accept(exception);
                }
            }
        }).start();
    }

    public CompletableFuture<T> submit() {
        return CompletableFuture.supplyAsync(this::execute);
    }

}
