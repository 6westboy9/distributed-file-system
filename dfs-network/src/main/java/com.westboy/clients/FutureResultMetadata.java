package com.westboy.clients;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureResultMetadata implements Future<Result> {

    private final Result result;

    public FutureResultMetadata(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result.completed();
    }

    @Override
    public Result get() throws InterruptedException, ExecutionException {
        result.await();
        return result;
    }

    @Override
    public Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean occurred = this.result.await(timeout, unit);
        if (!occurred) {
            throw new TimeoutException("Timeout after waiting for " + TimeUnit.MILLISECONDS.convert(timeout, unit) + " ms.");
        }
        return result;
    }
}
