package com.yyx.service;

import com.yyx.exception.WrongPwdException;
import com.yyx.exception.order.MealSoldOutException;
import com.yyx.exception.order.OverspentException;
import com.yyx.model.Order;
import com.yyx.model.Restaurant;
import com.yyx.model.criteria.OrderCriteria;

import java.util.List;
import java.util.Map;

public interface CustomerOrderService {

    /**
     * Can only retrieve restaurant info.
     * @param aid address id
     * @param distance in kilometers
     */
    List<Restaurant> getRestaurants(String email, long aid, double distance, int pageStart, int pageSize);

    /**
     * @return estimated delivery time in minutes
     */
    int estimateDeliveryTime(String email, long aid, String rid);

    double calcCost(String email, String rid, Map<Long, Integer> items);

    /**
     * @throws MealSoldOutException if there's one meal which has been sold out
     */
    void place(String email, String restaurant, long address, Map<Long, Integer> items);

    /**
     * @throws WrongPwdException if payment password is wrong
     * @throws OverspentException if balance not enough
     */
    void pay(String email, long oid, String pid, String paymentPwd);

    void complete(String email, long oid);

    /**
     * @return whether cancellation succeeds
     */
    boolean cancel(String email, long oid);

    List<Order> getPlaced(String email, int pageStart, int pageSize);

    List<Order> getPayedNotCompleted(String email, int pageStart, int pageSize);

    /**
     * @param criteria no need for rid and states
     */
    List<Order> getAll(OrderCriteria criteria);

    /**
     * @param criteria no need for rid and states
     */
    List<Order> getPayed(OrderCriteria criteria);

    /**
     * @param criteria no need for rid and states
     */
    List<Order> getCancelled(OrderCriteria criteria);

    Order get(String email, long oid);

}
