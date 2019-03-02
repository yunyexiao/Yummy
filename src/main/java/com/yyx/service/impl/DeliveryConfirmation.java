package com.yyx.service.impl;

import com.yyx.dao.OrderDao;
import com.yyx.model.Order;
import com.yyx.model.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableTransactionManagement
public class DeliveryConfirmation {
    private static final double[] LEVEL_COSTS = {300, 500, 1000, 3000, 5000, 8000, 10000, 30000};

    private OrderDao orderDao;
    private ModelValidator validator;

    @Autowired
    public DeliveryConfirmation(OrderDao orderDao, ModelValidator validator) {
        this.orderDao = orderDao;
        this.validator = validator;
    }

    @Transactional
    public void confirm(String email, long oid) {
        Order order = validator.getValidOrderByCustomer(email, oid);
        if(order.getState() != OrderState.DELIVERING) {
            throw new IllegalArgumentException("Order cannot be completed: " + oid);
        }
        order.setState(OrderState.COMPLETED);
        order.getCustomer().setLevel(calculateCustomerLevel(email));
    }

    private int calculateCustomerLevel(String email) {
        double sumCost = orderDao.sumConsumption(email);
        int level = 0;
        while(level < LEVEL_COSTS.length && LEVEL_COSTS[level] <= sumCost) {
            level++;
        }
        return level;
    }

}
