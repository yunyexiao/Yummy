package com.yyx.service.impl.task;

import java.util.function.Consumer;

public class OrderCancellationTask implements Runnable{
    private long oid;
    private Consumer<Long> cancel;

    OrderCancellationTask(long oid, Consumer<Long> cancel) {
        this.oid = oid;
        this.cancel = cancel;
    }

    @Override
    public void run() {
        cancel.accept(oid);
    }
}
