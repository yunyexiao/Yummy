package com.yyx.service.impl.task;

import com.yyx.service.impl.DeliveryConfirmation;
import com.yyx.service.impl.OrderCancellation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class OrderScheduler {
    private static final int CANCEL_DELAY = 2;
    private static final TimeUnit CANCEL_TIME_UNIT = TimeUnit.MINUTES;
    private static final int CONFIRM_DELAY = 24;
    private static final TimeUnit CONFIRM_TIME_UNIT = TimeUnit.HOURS;

    private ScheduledExecutorService scheduler;
    private Map<Long, ScheduledFuture<?>> cancellationFutures;
    private Map<Long, ScheduledFuture<?>> confirmationFutures;

    private OrderCancellation orderCancellation;
    private DeliveryConfirmation deliveryConfirmation;

    @Autowired
    public OrderScheduler(OrderCancellation orderCancellation, DeliveryConfirmation deliveryConfirmation) {
        scheduler = new ScheduledThreadPoolExecutor(10);
        cancellationFutures = new HashMap<>();
        confirmationFutures = new HashMap<>();
        this.orderCancellation = orderCancellation;
        this.deliveryConfirmation = deliveryConfirmation;
    }

    public void scheduleOrderCancellation(long oid, String email) {
        OrderCancellationTask task = new OrderCancellationTask(oid, id -> orderCancellation.cancel(email, id));
        cancellationFutures.put(oid, scheduler.schedule(task, CANCEL_DELAY, CANCEL_TIME_UNIT));
    }

    public void cancelOrderCancellation(long oid) {
        cancellationFutures.get(oid).cancel(true);
    }

    public void scheduleDeliveryConfirmation(long oid, String email) {
        DeliveryConfirmationTask task = new DeliveryConfirmationTask(oid, email,
                (id, cid) -> deliveryConfirmation.confirm(cid, id));
        confirmationFutures.put(oid, scheduler.schedule(task, CONFIRM_DELAY, CONFIRM_TIME_UNIT));
    }

    public void cancelDeliveryConfirmation(long oid) {
        confirmationFutures.get(oid).cancel(false);
    }
}
