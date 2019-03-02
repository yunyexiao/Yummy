package com.yyx.dao;

import com.yyx.model.Order;
import com.yyx.model.criteria.OrderCriteria;
import lombok.NonNull;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.util.List;

public interface OrderDao {
    void insert(@NonNull Order order);

    @NonNull Order find(long id);

    /**
     * Get the sell number of the given meal in the given date.
     */
    int countMeal(long mid, Date date);

    /**
     * @throws NoResultException if no result found
     */
    @NonNull List<Order> find(@NonNull OrderCriteria criteria);

    double sumConsumption(String email);
}
