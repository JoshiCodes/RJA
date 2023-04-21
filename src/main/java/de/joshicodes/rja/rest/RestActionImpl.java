package de.joshicodes.rja.rest;

import de.joshicodes.rja.RJA;

import java.util.function.Function;

public class RestActionImpl<T> extends RestAction<T> {

    private Function<Void, T> function;

    public RestActionImpl(RJA rja, boolean async, Function<Void, T> function) {
        super(rja, async);
        this.function = function;
    }

    public RestActionImpl(RJA rja, Function<Void, T> function) {
        this(rja, false, function);
    }

    @Override
    protected T execute() {
        return function.apply(null);
    }

}
