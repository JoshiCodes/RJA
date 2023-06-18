package de.joshicodes.rja.rest;

import de.joshicodes.rja.RJA;
import de.joshicodes.rja.util.Pair;

import java.util.function.Supplier;

public class SimpleRestAction<R> extends RestAction<R> {

    private final Supplier<R> run;

    public SimpleRestAction(RJA rja, Supplier<R> run) {
        super(rja, null);
        this.run = run;
    }

    @Override
    protected Pair<Long, R> execute() throws Exception {
        return new Pair<>(-1L, run.get());
    }

}
