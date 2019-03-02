package com.yyx.dao.impl;

import com.yyx.model.Order;
import com.yyx.model.OrderState;
import com.yyx.model.Transaction;
import com.yyx.model.criteria.OrderCriteria;
import com.yyx.model.criteria.TransactionCriteria;
import org.hibernate.Session;

import javax.persistence.TypedQuery;

final class QueryHelper {
    private QueryHelper() {
        // empty constructor
    }

    static TypedQuery<Order> orderQuery(OrderCriteria criteria, Session session) {
        TypedQuery<Order> query = session.createQuery(orderQueryString(criteria), Order.class);
        if(criteria.getEmail() != null) {
            query.setParameter("email", criteria.getEmail());
        }
        if(criteria.getRid() != null) {
            query.setParameter("rid", criteria.getRid());
        }
        if(criteria.getStartTime() != null) {
            query.setParameter("startTime", criteria.getStartTime());
        }
        if(criteria.getEndTime() != null) {
            query.setParameter("endTime", criteria.getEndTime());
        }
        if(criteria.getCostBottom() >= 0) {
            query.setParameter("costBottom", criteria.getCostBottom());
        }
        if(criteria.getCostTop() >= 0) {
            query.setParameter("costTop", criteria.getCostTop());
        }
        if(criteria.getStates() != null && !criteria.getStates().equals(OrderState.ALL_STATES)) {
            query.setParameter("states", criteria.getStates());
        }
        if(criteria.getPageStart() >= 0 && criteria.getPageSize() >= 0) {
            query.setFirstResult(criteria.getPageStart()).setMaxResults(criteria.getPageSize());
        }
        return query;
    }

    static TypedQuery<Transaction> transactionQuery(TransactionCriteria criteria, Session session) {
        TypedQuery<Transaction> query = session.createQuery(transactionQueryString(criteria), Transaction.class);
        if (criteria.getAccount() != null) {
            query.setParameter("account", criteria.getAccount());
        }
        if (criteria.getStartTime() != null) {
            query.setParameter("startTime", criteria.getStartTime());
        }
        if (criteria.getEndTime() != null) {
            query.setParameter("endTime", criteria.getEndTime());
        }
        if(criteria.getPageStart() >= 0 && criteria.getPageSize() >= 0) {
            query.setFirstResult(criteria.getPageStart()).setMaxResults(criteria.getPageSize());
        }
        return query;
    }

    private static String orderQueryString(OrderCriteria criteria) {
        StringBuilder hqlBuilder = new StringBuilder("from Order ");
        boolean whereStart = false;
        if(criteria.getEmail() != null) {
            hqlBuilder.append("where customer.email = :email ");
            whereStart = true;
        }
        if(criteria.getRid() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("restaurant.id = :rid ");
            whereStart = true;
        }
        if(criteria.getStartTime() != null && criteria.getEndTime() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("placeTime between :startTime and :endTime ");
            whereStart = true;
        } else if(criteria.getStartTime() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("placeTime >= :startTime ");
            whereStart = true;
        } else if(criteria.getEndTime() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("placeTime <= :endTime ");
            whereStart = true;
        }
        if(criteria.getCostBottom() >= 0 && criteria.getCostTop() >= 0) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("cost between :costBottom and :costTop ");
            whereStart = true;
        } else if (criteria.getCostBottom() >= 0) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("cost >= :costBottom ");
            whereStart = true;
        } else if (criteria.getCostTop() >= 0) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("cost <= :costTop ");
            whereStart = true;
        }
        if(criteria.getStates() != null && !criteria.getStates().equals(OrderState.ALL_STATES)) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("state in :states ");
        } // 'where' clause ends here
        // 'order by' clause starts here
        boolean orderStart = false;
        if(criteria.isGrouped()) {
            hqlBuilder.append("order by ");
            hqlBuilder.append(criteria.getEmail() == null ? "customer.email asc " : "restaurant.id asc");
            orderStart = true;
        }
        if(criteria.getStartTime() != null || criteria.getEndTime() != null) {
            hqlBuilder.append(orderStart ? ", " : "order by ");
            hqlBuilder.append("placeTime desc ");
            orderStart = true;
        }
        if(criteria.getCostTop() >= 0 || criteria.getCostBottom() >= 0) {
            hqlBuilder.append(orderStart ? ", " : "order by ");
            hqlBuilder.append("cost desc ");
        } // 'order by' clause ends here
        return hqlBuilder.toString();
    }

    private static String transactionQueryString(TransactionCriteria criteria) {
        StringBuilder hqlBuilder = new StringBuilder("from Transaction ");
        boolean whereStart = false;
        if (criteria.getAccount() != null) {
            hqlBuilder.append("where (outAccount = :account or inAccount = :account) ");
            whereStart = true;
        }
        if(criteria.getStartTime() != null && criteria.getEndTime() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("time between :startTime and :endTime ");
        } else if(criteria.getStartTime() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("time >= :startTime ");
        } else if (criteria.getEndTime() != null) {
            hqlBuilder.append(whereStart ? "and " : "where ");
            hqlBuilder.append("time <= :endTime ");
        }
        if(criteria.getStartTime() != null || criteria.getEndTime() != null) {
            hqlBuilder.append("order by time asc ");
        }
        return hqlBuilder.toString();
    }
}
