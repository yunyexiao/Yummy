package com.yyx.service;

import com.yyx.model.Order;
import com.yyx.model.criteria.OrderCriteria;

import java.util.List;

public interface RestaurantOrderService {

    List<Order> getUnaccepted(String rid, int pageStart, int pageSize);

    void accept(String rid, long oid);

    List<Order> getUndelivered(String rid, int pageStart, int pageSize);

    void deliver(String rid, long oid);

    List<Order> getDeliveryRecords(String rid, int pageStart, int pageSize);

    /**
     * @param criteria no need for email and states
     */
    List<Order> getAccepted(OrderCriteria criteria);

    /**
     * @param criteria no need for email and states
     */
    List<Order> getCancelled(OrderCriteria criteria);

    Order get(String rid, long oid);
}
