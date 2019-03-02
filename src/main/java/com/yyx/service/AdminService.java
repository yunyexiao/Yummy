package com.yyx.service;

import com.yyx.model.Order;
import com.yyx.model.Restaurant;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface AdminService {

    /**
     * @throws com.yyx.exception.WrongPwdException if password is wrong
     */
    void signIn(String pwd);

    /**
     * Retrieve all completed or canceling orders.
     */
    List<Order> getUnsettledOrders(int pageStart, int pageSize);

    /**
     * Settle a completed order, or cancel a canceling order.
     */
    void settleOrder(long oid);

    List<Restaurant> getInvalidRestaurants(int pageStart, int pageSize);

    List<Restaurant> getDraftedRestaurants(int pageStart, int pageSize);

    /**
     * @param approved true to approve this restaurant, false to deny it
     */
    void checkRestaurant(String rid, boolean approved);

    Restaurant getUncheckedRestaurant(String rid);

    long getCustomerCount();

    long getRestaurantCount();

    /**
     * @return the income and outcome of each month in the given range
     */
    Map<YearMonth, Map<String, Double>> getFinancialAffairs(YearMonth start, YearMonth end);
}
