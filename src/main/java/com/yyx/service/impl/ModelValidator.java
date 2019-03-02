package com.yyx.service.impl;

import com.yyx.dao.CustomerDao;
import com.yyx.dao.OrderDao;
import com.yyx.dao.RestaurantDao;
import com.yyx.exception.WrongPwdException;
import com.yyx.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This class should be used in a service method with @Transactional annotated.
 */
@Component
class ModelValidator {
    private CustomerDao customerDao;
    private RestaurantDao restaurantDao;
    private OrderDao orderDao;

    @Autowired
    public ModelValidator(CustomerDao customerDao, RestaurantDao restaurantDao, OrderDao orderDao) {
        this.customerDao = customerDao;
        this.restaurantDao = restaurantDao;
        this.orderDao = orderDao;
    }

    Customer getValidCustomer(String email) {
        Customer customer = customerDao.find(email);
        checkCustomerValid(customer);
        return customer;
    }

    void checkCustomerValid(String email) {
        checkCustomerValid(customerDao.find(email));
    }

    Restaurant getValidRestaurant(String rid) {
        Restaurant restaurant = restaurantDao.find(rid);
        if(restaurant.isValid()) {
            return restaurant;
        } else {
            throw new IllegalArgumentException("Restaurant is invalid: " + rid);
        }
    }

    AddressInfo getValidAddress(String email, long aid) {
        return getValidAddress(getValidCustomer(email), aid);
    }

    /**
     * Check for payment account existence and id-pwd match.
     */
    PaymentAccount getValidPaymentAccount(Customer customer, String pid, String pwd) {
        Optional<PaymentAccount> optionalAccount = customer.getAccounts().stream()
                .filter(a -> a.getId().equals(pid)).findFirst();
        if(optionalAccount.isPresent()) {
            PaymentAccount paymentAccount = optionalAccount.get();
            if(paymentAccount.getPwd().equals(pwd)) {
                return paymentAccount;
            } else {
                throw new WrongPwdException(pid, pwd);
            }
        } else {
            throw new IllegalArgumentException("No such payment account: " + pid);
        }
    }

    /**
     * @param customer not checked
     */
    AddressInfo getValidAddress(Customer customer, long aid) {
        Optional<AddressInfo> optionalAddress = customer.getAddressInfos().stream()
                .filter(a -> a.getId() == aid).findFirst();
        if(optionalAddress.isPresent()) {
            return optionalAddress.get();
        } else {
            throw new IllegalArgumentException("Address not found: " + aid);
        }
    }

    /**
     * Check for valid customer and order-customer match.
     */
    Order getValidOrderByCustomer(String email, long oid) {
        Order order = orderDao.find(oid);
        Customer customer = order.getCustomer();
        checkCustomerValid(customer);
        if(customer.getEmail().equals(email)) {
            return order;
        } else {
            throw new IllegalArgumentException("Order customer not match: " + oid + "-" + email);
        }
    }

    Order getValidOrderByRestaurant(String rid, long oid) {
        Order order = orderDao.find(oid);
        Restaurant restaurant = order.getRestaurant();
        checkRestaurantValid(restaurant);
        if(restaurant.getId().equals(rid)) {
            return order;
        } else {
            throw new IllegalArgumentException("Order restaurant not match: " + oid + "-" + rid);
        }
    }

    private void checkCustomerValid(Customer customer) {
        if(!customer.isValid()) {
            throw new IllegalArgumentException("Invalid customer: " + customer.getEmail());
        }
    }

    private void checkRestaurantValid(Restaurant restaurant) {
        if(!restaurant.isValid()) {
            throw new IllegalArgumentException("Invalid restaurant: " + restaurant.getId());
        }
    }

}
