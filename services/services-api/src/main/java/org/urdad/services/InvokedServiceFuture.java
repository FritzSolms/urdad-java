package org.urdad.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InvokedServiceFuture implements Future<Response> {

    private volatile Response response;
    private volatile Throwable throwable;
    private volatile boolean done = false;

    public void complete(Response response) {
        this.response = response;
        done = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void completeExceptionally(Throwable throwable) {
        this.throwable = throwable;
        done = true;
        synchronized (this) {
            this.notifyAll();
        }
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
        return done;
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            synchronized (this) {
                this.wait();
            }
        }
        if (!isDone()){
            throw new RuntimeException("Response not received.");
        } else {
            if(response != null){
                return response;
            } else if(throwable != null){
                throw new ExecutionException(throwable);
            } else {
                throw new RuntimeException("Response not received.");
            }
        }
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!isDone()) {
            if(unit != null) {
                synchronized (this) {
                    this.wait(unit.toMillis(timeout));
                }
            } else {
                synchronized (this) {
                    this.wait(timeout);
                }
            }
        }
        if (!isDone()){
            throw new TimeoutException("Response not received.");
        } else {
            if(response != null){
                return response;
            } else if(throwable != null){
                throw new ExecutionException(throwable);
            } else {
                throw new RuntimeException("Response not received.");
            }
        }
    }
}
