package com.yyx.service.impl;

import com.yyx.dao.*;
import com.yyx.exception.WrongPwdException;
import com.yyx.model.*;
import com.yyx.model.criteria.OrderCriteria;
import com.yyx.model.criteria.TransactionCriteria;
import com.yyx.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@EnableTransactionManagement
public class AdminServiceImpl implements AdminService {
    private static final String ADMIN_KEY_NAME = "adminKey";
    private static final double RESTAURANT_INCOME_RATE = 0.7;

    private CustomerDao customerDao;
    private RestaurantDao restaurantDao;
    private OrderDao orderDao;
    private TransactionDao transactionDao;
    private PropertyDao propertyDao;
    private OrderCancellation orderCancellation;

    @Autowired
    public AdminServiceImpl(CustomerDao customerDao, RestaurantDao restaurantDao,
                            OrderDao orderDao, TransactionDao transactionDao,
                            PropertyDao propertyDao, OrderCancellation orderCancellation) {
        this.customerDao = customerDao;
        this.restaurantDao = restaurantDao;
        this.orderDao = orderDao;
        this.transactionDao = transactionDao;
        this.propertyDao = propertyDao;
        this.orderCancellation = orderCancellation;
    }

    @Override
    @Transactional(readOnly = true)
    public void signIn(String pwd) {
        String key = propertyDao.find(ADMIN_KEY_NAME).getValue();
        if(!key.equals(pwd)) {
            throw new WrongPwdException("admin", pwd);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUnsettledOrders(int pageStart, int pageSize) {
        OrderCriteria criteria = new OrderCriteria();
        criteria.setStates(Arrays.asList(OrderState.COMPLETED, OrderState.CANCELING));
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return orderDao.find(criteria);
    }

    @Override
    @Transactional
    public void settleOrder(long oid) {
        Order order = orderDao.find(oid);
        switch (order.getState()) {
            case OrderState.COMPLETED: {
                Transaction transaction = transactionDao.findPayment(oid);
                double income = transaction.getAmount();
                transactionDao.insert(Transaction.builder()
                        .order(order)
                        .outAccount("YUMMY")
                        .inAccount(order.getRestaurant().getId())
                        .amount(income * RESTAURANT_INCOME_RATE)
                        .time(new Timestamp(System.currentTimeMillis()))
                        .build());
                order.setState(OrderState.SETTLED);
                break;
            }
            case OrderState.CANCELING: {
                orderCancellation.adminCancel(order);
                break;
            }
            default:
                throw new IllegalArgumentException("This order cannot be settled: " + oid);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getInvalidRestaurants(int pageStart, int pageSize) {
        return restaurantDao.findInvalid(pageStart, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getDraftedRestaurants(int pageStart, int pageSize) {
        return restaurantDao.findDrafted(pageStart, pageSize);
    }

    @Override
    @Transactional
    public void checkRestaurant(String rid, boolean approved) {
        Restaurant restaurant = restaurantDao.find(rid);
        if(restaurant.getValid() == 0) {
            if(approved) {
                restaurant.setValid(1);
            } else {
                restaurantDao.delete(rid);
            }
        } else {
            if(approved) {
                RestaurantDraft draft = restaurant.getDraft();
                restaurant.setName(draft.getName());
                restaurant.setDescription(draft.getDescription());
                restaurant.setType(draft.getType());
                restaurant.setAddress(draft.getAddress());
                restaurant.setLatitude(draft.getLatitude());
                restaurant.setLongitude(draft.getLongitude());
                restaurant.setPhone(draft.getPhone());
            }
            restaurant.setDraft(null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant getUncheckedRestaurant(String rid) {
        Restaurant restaurant = restaurantDao.find(rid);
        if(restaurant.getValid() == 1 && restaurant.getDraft() == null) {
            throw new IllegalArgumentException("This restaurant is checked: " + rid);
        }
        return restaurant;
    }

    @Override
    @Transactional(readOnly = true)
    public long getCustomerCount() {
        return customerDao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getRestaurantCount() {
        return restaurantDao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<YearMonth, Map<String, Double>> getFinancialAffairs(YearMonth start, YearMonth end) {
        System.out.println(start + "\t\t" + end);
        if(start.isAfter(end)) {
            throw new IllegalArgumentException("Start time " + start + " is later than end time " + end + ".");
        }
        List<String[]> rawData = transactionDao.getStatistics(start, end);
        final String[] types = {"income", "outcome"};
        int i = 0;
        Map<YearMonth, Map<String, Double>> financialData = new HashMap<>();
        for(YearMonth month = start; month.compareTo(end) <= 0; month = month.plusMonths(1)) {
            Map<String, Double> monthlyData = new HashMap<>(2);
            for (String type: types) {
                String[] row;
                if (i < rawData.size() && month.getYear() == Integer.parseInt((row = rawData.get(i))[0])
                        && month.getMonthValue() == Integer.parseInt(row[1])
                        && type.equals(row[2])) {
                    monthlyData.put(type, Double.parseDouble(row[3]));
                    i++;
                } else {
                    monthlyData.put(type, 0.0D);
                }
            }
            financialData.put(month, monthlyData);
        }
        return financialData;
    }
}
