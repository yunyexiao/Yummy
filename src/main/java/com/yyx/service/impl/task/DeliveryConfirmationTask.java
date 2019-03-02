package com.yyx.service.impl.task;

import java.util.function.BiConsumer;

public class DeliveryConfirmationTask implements Runnable{
    private long oid;
    private String email;
    private BiConsumer<Long, String> confirm;

    DeliveryConfirmationTask(long oid, String email, BiConsumer<Long, String> confirm) {
        this.oid = oid;
        this.email = email;
        this.confirm = confirm;
    }

    @Override
    public void run() {
        confirm.accept(oid, email);
    }

}
