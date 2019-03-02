package com.yyx.model.criteria;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderCriteria {
    private String email;
    private String rid;
    private Timestamp startTime;
    private Timestamp endTime;
    private double costBottom = -1;
    private double costTop = -1;
    private int pageStart = -1;
    private int pageSize = -1;
    private List<Integer> states;
    /**
     * Whether the results should be grouped by customers | restaurants.
     */
    private boolean grouped;

    /**
     * Only copy time, cost, page and grouped conditions. Not copy email, rid and states conditions.
     */
    public OrderCriteria copyNormalConditions() {
        OrderCriteria criteria = new OrderCriteria();
        criteria.startTime = startTime;
        criteria.endTime = endTime;
        criteria.costBottom = costBottom;
        criteria.costTop = costTop;
        criteria.pageStart = pageStart;
        criteria.pageSize = pageSize;
        criteria.grouped = grouped;
        return criteria;
    }
}
