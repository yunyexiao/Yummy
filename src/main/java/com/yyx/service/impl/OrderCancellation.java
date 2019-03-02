package com.yyx.service.impl;

import com.yyx.dao.TransactionDao;
import com.yyx.model.Order;
import com.yyx.model.OrderState;
import com.yyx.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@EnableTransactionManagement
public class OrderCancellation {
    private static final int[] CANCEL_TIME = {1, 2, 3, 4, 5}; // in minutes
    private static final double[] CANCEL_RETURNS = {1.0, 0.8, 0.6, 0.4, 0.2};
    private static final double RESTAURANT_INCOME_RATE = 0.7;
    private static final int MILLI_IN_MINUTE = 60000;

    private TransactionDao transactionDao;
    private ModelValidator validator;

    @Autowired
    public OrderCancellation(ModelValidator validator, TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
        this.validator = validator;
    }

    @Transactional
    public boolean cancel(String email, long oid) {
        Order order = validator.getValidOrderByCustomer(email, oid);
        if(order.getState() == OrderState.PLACED) {
            order.setState(OrderState.CANCELLED);
            order.setCancelTime(new Timestamp(System.currentTimeMillis()));
            return true;
        } else if(order.getState() == OrderState.PAYED || order.getState() == OrderState.ACCEPTED) {
            Timestamp cancelTime = new Timestamp(System.currentTimeMillis());
            Transaction transaction = transactionDao.findPayment(oid);
            if (this.canCancel(transaction.getTime(), cancelTime)) {
                order.setState(OrderState.CANCELING);
                order.setCancelTime(cancelTime);
            }
            return true;
        }
        return false;
    }

    @Transactional
    public void adminCancel(Order order) {
        Transaction transaction = transactionDao.findPayment(order.getId());
        int diffInMin = (int) ((order.getCancelTime().getTime() - transaction.getTime().getTime()) / MILLI_IN_MINUTE);
        int index = 0;
        while(index < CANCEL_TIME.length && CANCEL_TIME[index] <= diffInMin) {
            index++;
        }
        if(index >= CANCEL_TIME.length) {
            throw new IllegalArgumentException("This order cannot be cancelled: " + order.getId());
        }
        // transaction back to customer
        transactionDao.insert(Transaction.builder()
                .order(order)
                .outAccount("YUMMY")
                .inAccount(transaction.getOutAccount())
                .amount(transaction.getAmount() * CANCEL_RETURNS[index])
                .time(new Timestamp(System.currentTimeMillis()))
                .build());
        // transaction to restaurant if having income
        if(index > 0) {
            double income = transaction.getAmount() * (1 - CANCEL_RETURNS[index]);
            transactionDao.insert(Transaction.builder()
                    .order(order)
                    .outAccount("YUMMY")
                    .inAccount(order.getRestaurant().getId())
                    .amount(income * RESTAURANT_INCOME_RATE)
                    .time(new Timestamp(System.currentTimeMillis()))
                    .build());
        }
        order.setState(OrderState.CANCELLED);
    }

    private boolean canCancel(Timestamp paymentTime, Timestamp cancelTime) {
        long diffInMin = (cancelTime.getTime() - paymentTime.getTime()) / MILLI_IN_MINUTE;
        int maxCancelTime = CANCEL_TIME[CANCEL_TIME.length - 1];
        return diffInMin < maxCancelTime;
    }
}
