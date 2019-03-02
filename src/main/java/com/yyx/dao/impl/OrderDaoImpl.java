package com.yyx.dao.impl;

import com.yyx.dao.OrderDao;
import com.yyx.model.Order;
import com.yyx.model.criteria.OrderCriteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao {
    private SessionFactory factory;

    @Autowired
    public OrderDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }
    @Override
    public void insert(Order order) {
        factory.getCurrentSession().persist(order);
    }

    @Override
    public Order find(long id) {
        Order order = factory.getCurrentSession().find(Order.class, id);
        if(null != order) {
            return order;
        } else {
            throw new NoResultException("Order not found: " + id);
        }
    }

    @Override
    public int countMeal(long mid, Date date) {
        TypedQuery<Long> query = factory.getCurrentSession().createNamedQuery("OrderItem.countMeal", Long.class)
                .setParameter("mid", mid)
                .setParameter("date", date);
        Long result = query.getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    @Override
    public List<Order> find(OrderCriteria criteria) {
        return QueryHelper.orderQuery(criteria, factory.getCurrentSession()).getResultList();
    }

    @Override
    public double sumConsumption(String email) {
        return factory.getCurrentSession().createNamedQuery("Order.sumConsumption", Double.class)
                .setParameter("email", email)
                .getSingleResult();
    }
}
