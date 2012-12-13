package com.service.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {
    private int counter = 0;

    public DefaultThreadPoolExecutor() {
        super(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public synchronized void execute(Runnable command) {
        counter++;
        super.execute(command);
    }

    @Override
    protected synchronized void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        counter--;
        notifyAll();
    }

    public synchronized void waitForExecuted() throws InterruptedException {
        while (counter == 0)
            wait();
    }
}