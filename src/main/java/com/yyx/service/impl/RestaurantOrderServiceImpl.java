package com.yyx.service.impl;

import com.yyx.dao.OrderDao;
import com.yyx.model.Order;
import com.yyx.model.OrderState;
import com.yyx.model.criteria.OrderCriteria;
import com.yyx.service.RestaurantOrderService;
import com.yyx.service.impl.task.OrderScheduler;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@EnableTransactionManagement
public class RestaurantOrderServiceImpl implements RestaurantOrderService {
    private OrderDao orderDao;
    private ModelValidator validator;
    private OrderScheduler scheduler;

    @Autowired
    public RestaurantOrderServiceImpl(OrderDao orderDao, ModelValidator validator,
                                      OrderScheduler scheduler) {
        this.orderDao = orderDao;
        this.validator = validator;
        this.scheduler = scheduler;
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getUnaccepted(String rid, int pageStart, int pageSize) {
        OrderCriteria criteria = new OrderCriteria();
        criteria.setRid(rid);
        criteria.setStates(Collections.singletonList(OrderState.PAYED));
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return orderDao.find(criteria);
    }

    @Override @Transactional
    public void accept(String rid, long oid) {
        Order order = validator.getValidOrderByRestaurant(rid, oid);
        order.setState(OrderState.ACCEPTED);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getUndelivered(String rid, int pageStart, int pageSize) {
        OrderCriteria criteria = new OrderCriteria();
        criteria.setRid(rid);
        criteria.setStates(Collections.singletonList(OrderState.ACCEPTED));
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return orderDao.find(criteria);
    }

    @Override @Transactional
    public void deliver(String rid, long oid) {
        Order order = validator.getValidOrderByRestaurant(rid, oid);
        order.setState(OrderState.DELIVERING);
        scheduler.scheduleDeliveryConfirmation(oid, order.getCustomer().getEmail());
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getDeliveryRecords(String rid, int pageStart, int pageSize) {
        OrderCriteria criteria = new OrderCriteria();
        criteria.setRid(rid);
        criteria.setStates(Collections.singletonList(OrderState.DELIVERING));
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return orderDao.find(criteria);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getAccepted(OrderCriteria criteria) {
        OrderCriteria realCriteria = criteria.copyNormalConditions();
        realCriteria.setRid(criteria.getRid());
        realCriteria.setStates(OrderState.ACCEPT_STATES);
        return orderDao.find(criteria);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getCancelled(OrderCriteria criteria) {
        OrderCriteria realCriteria = criteria.copyNormalConditions();
        realCriteria.setRid(criteria.getRid());
        realCriteria.setStates(OrderState.CANCEL_STATES);
        return orderDao.find(criteria);
    }

    @Override @Transactional(readOnly = true)
    public Order get(String rid, long oid) {
        Order order = validator.getValidOrderByRestaurant(rid, oid);
        Hibernate.initialize(order.getItems());
        return order;
    }
}
