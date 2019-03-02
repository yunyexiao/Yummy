package com.yyx.service.impl;

import com.yyx.dao.OrderDao;
import com.yyx.dao.RestaurantDao;
import com.yyx.dao.TransactionDao;
import com.yyx.exception.order.MealSoldOutException;
import com.yyx.exception.order.OverspentException;
import com.yyx.model.*;
import com.yyx.model.criteria.OrderCriteria;
import com.yyx.service.CustomerOrderService;
import com.yyx.service.impl.task.OrderScheduler;
import com.yyx.util.DistanceUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Service
@EnableTransactionManagement
public class CustomerOrderServiceImpl implements CustomerOrderService {
    private static final double[] LEVEL_DISCOUNT_RATES = {1.0, 0.95, 0.9, 0.85, 0.8, 0.7, 0.65, 0.6, 0.5};

    private RestaurantDao restaurantDao;
    private OrderDao orderDao;
    private TransactionDao transactionDao;
    private ModelValidator validator;
    private OrderScheduler scheduler;
    private OrderCancellation orderCancellation;
    private DeliveryConfirmation deliveryConfirmation;

    @Autowired
    public CustomerOrderServiceImpl(RestaurantDao restaurantDao, OrderDao orderDao,
                                    TransactionDao transactionDao, ModelValidator validator,
                                    OrderScheduler scheduler, OrderCancellation orderCancellation,
                                    DeliveryConfirmation deliveryConfirmation) {
        this.restaurantDao = restaurantDao;
        this.orderDao = orderDao;
        this.transactionDao = transactionDao;
        this.validator = validator;
        this.scheduler = scheduler;
        this.orderCancellation = orderCancellation;
        this.deliveryConfirmation = deliveryConfirmation;
    }

    @Override @Transactional(readOnly = true)
    public int estimateDeliveryTime(String email, long aid, String rid) {
        AddressInfo destination = validator.getValidAddress(email, aid);
        Restaurant restaurant = validator.getValidRestaurant(rid);
        double distance = DistanceUtil.distance(restaurant.getLatitude(), restaurant.getLongitude(),
                destination.getLatitude(), destination.getLongitude());
        return 30 + (int) distance * 2;
    }

    @Override @Transactional(readOnly = true)
    public double calcCost(String email, String rid, Map<Long, Integer> items) {
        Customer customer = validator.getValidCustomer(email);
        Restaurant restaurant = validator.getValidRestaurant(rid);
        List<OrderItem> orderItems = getValidOrderItems(items, restaurant.getMeals());
        return calculateCost(orderItems, customer.getLevel(), restaurant.getPromotions());
    }

    @Override @Transactional
    public void place(String email, String rid, long aid, Map<Long, Integer> items) {
        Customer customer = validator.getValidCustomer(email);
        AddressInfo addressInfo = validator.getValidAddress(customer, aid);
        Restaurant restaurant = validator.getValidRestaurant(rid);
        List<OrderItem> orderItems = getValidOrderItems(items, restaurant.getMeals());

        // insert a new order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setDestination(addressInfo);
        order.setState(OrderState.PLACED);
        order.setPlaceTime(new Timestamp(System.currentTimeMillis()));
        orderItems.forEach(orderItem -> orderItem.setOrd(order));
        order.setItems(orderItems);
        order.setCost(calculateCost(order.getItems(), customer.getLevel(), restaurant.getPromotions()));
        orderDao.insert(order);

        // add a timing task
        scheduler.scheduleOrderCancellation(order.getId(), email);
    }

    @Override @Transactional
    public void pay(String email, long oid, String pid, String paymentPwd) {
        Order order = validator.getValidOrderByCustomer(email, oid);
        Customer customer = order.getCustomer();
        // check for valid order state
        if(order.getState() != OrderState.PLACED) {
            throw new IllegalArgumentException("Cannot pay this order: " + oid);
        }
        PaymentAccount account = validator.getValidPaymentAccount(customer, pid, paymentPwd);
        // check for enough balance
        if(account.getBalance() < order.getCost()) {
            throw new OverspentException(pid);
        }

        // subtract customer's balance
        account.setBalance(account.getBalance() - order.getCost());
        // insert a new transaction record
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setOutAccount(pid);
        transaction.setInAccount("YUMMY");
        transaction.setAmount(order.getCost());
        transaction.setTime(new Timestamp(System.currentTimeMillis()));
        transactionDao.insert(transaction);
        // update order's state
        order.setState(OrderState.PAYED);
        // cancel order's timing task
        scheduler.cancelOrderCancellation(oid);
    }

    @Override @Transactional
    public void complete(String email, long oid) {
        deliveryConfirmation.confirm(email, oid);
        scheduler.cancelDeliveryConfirmation(oid);
    }

    @Override @Transactional
    public boolean cancel(String email, long oid) {
        return orderCancellation.cancel(email, oid);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getPlaced(String email, int pageStart, int pageSize) {
        List<Integer> states = Collections.singletonList(OrderState.PLACED);
        return getOrders(email, states, pageStart, pageSize);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getPayedNotCompleted(String email, int pageStart, int pageSize) {
        List<Integer> states = Arrays.asList(OrderState.PAYED, OrderState.ACCEPTED, OrderState.DELIVERING);
        return getOrders(email, states, pageStart, pageSize);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getAll(OrderCriteria criteria) {
        return getOrders(criteria, OrderState.ALL_STATES);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getPayed(OrderCriteria criteria) {
        return getOrders(criteria, OrderState.PAY_STATES);
    }

    @Override @Transactional(readOnly = true)
    public List<Order> getCancelled(OrderCriteria criteria) {
        return getOrders(criteria, OrderState.CANCEL_STATES);
    }

    @Override @Transactional(readOnly = true)
    public Order get(String email, long oid) {
        Order order = validator.getValidOrderByCustomer(email, oid);
        Hibernate.initialize(order.getItems());
        return order;
    }

    @Override @Transactional(readOnly = true)
    public List<Restaurant> getRestaurants(String email, long aid, double distance, int pageStart, int pageSize) {
        AddressInfo addressInfo = validator.getValidAddress(email, aid);
        return restaurantDao.find(addressInfo, distance, pageStart, pageSize);
    }

    // ------------------------------ private methods ----------------------------------

    private List<OrderItem> getValidOrderItems(Map<Long, Integer> items, List<Meal> meals) {
        final Date now = new Date(System.currentTimeMillis());
        final List<OrderItem> result = new ArrayList<>(items.size());
        items.forEach((mid, num) -> {
            Optional<Meal> optionalMeal = meals.stream().filter(m -> m.getId() == mid).findFirst();
            if(!optionalMeal.isPresent()) {
                throw new IllegalArgumentException("Meal not found: " + mid);
            }
            Meal meal = optionalMeal.get();
            boolean enough = meal.getNum() >= orderDao.countMeal(mid, now) + num;
            if(!enough) {
                throw new MealSoldOutException(mid);
            }
            OrderItem item = new OrderItem();
            item.setMeal(meal);
            item.setNum(num);
            result.add(item);
        });
        return result;
    }

    private double calculateCost(List<OrderItem> items, int level, List<Promotion> promotions) {
        final double cost = items.stream().mapToDouble(i -> i.getMeal().getPrice() * i.getNum()).sum()
                * LEVEL_DISCOUNT_RATES[level];
        Optional<Promotion> optionalPromotion = promotions.stream().filter(p -> p.getAmount() <= cost)
                .max(Comparator.comparingDouble(Promotion::getAmount));
        double discount = 0;
        if(optionalPromotion.isPresent()) {
            discount = optionalPromotion.get().getDiscount();
        }
        return cost - discount;
    }

    private List<Order> getOrders(String email, List<Integer> states, int pageStart, int pageSize) {
        validator.checkCustomerValid(email);

        OrderCriteria criteria = new OrderCriteria();
        criteria.setEmail(email);
        criteria.setStates(states);
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return orderDao.find(criteria);
    }

    private List<Order> getOrders(OrderCriteria criteria, List<Integer> states) {
        validator.checkCustomerValid(criteria.getEmail());

        OrderCriteria realCriteria = criteria.copyNormalConditions();
        realCriteria.setEmail(criteria.getEmail());
        realCriteria.setStates(states);
        return orderDao.find(realCriteria);
    }
}