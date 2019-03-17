package com.yyx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yyx.dao.OrderDao;
import com.yyx.dao.RestaurantDao;
import com.yyx.dao.TransactionDao;
import com.yyx.exception.AccountNotFoundException;
import com.yyx.exception.InvalidActionException;
import com.yyx.exception.WrongPwdException;
import com.yyx.model.*;
import com.yyx.model.criteria.TransactionCriteria;
import com.yyx.service.RestaurantService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableTransactionManagement
public class RestaurantServiceImpl implements RestaurantService {
    private RestaurantDao restaurantDao;
    private TransactionDao transactionDao;
    private OrderDao orderDao;
    private RidGenerator generator;

    @Autowired
    public RestaurantServiceImpl(RestaurantDao restaurantDao, TransactionDao transactionDao,
                                 OrderDao orderDao, RidGenerator generator) {
        this.restaurantDao = restaurantDao;
        this.transactionDao = transactionDao;
        this.orderDao = orderDao;
        this.generator = generator;
    }

    @Override @Transactional
    public String signUp(RestaurantDraft info, String pwd) {
        Restaurant restaurant = new Restaurant();
        copyInfo(info, restaurant);
        restaurant.setId(generator.generate());
        restaurant.setPwd(pwd);
        restaurant.setBalance(0);
        restaurant.setValid(0);
        restaurantDao.insert(restaurant);
        return restaurant.getId();
    }

    @Override @Transactional(readOnly = true)
    public void signIn(String id, String pwd) {
        Restaurant restaurant;
        try {
            restaurant = restaurantDao.find(id);
        } catch (NoResultException e) {
            throw new AccountNotFoundException(id);
        }
        // A restaurant can sign in even if invalid, so no need to check for validness.
        if(!restaurant.getPwd().equals(pwd)) {
            throw new WrongPwdException(id, pwd);
        }
    }

    @Override @Transactional(readOnly = true)
    public List<Restaurant> searchByName(String name, int pageStart, int pageSize) {
        return restaurantDao.findByName(name, pageStart, pageSize);
    }

    @Override @Transactional
    public void modifyInfo(String id, RestaurantDraft info) {
        Restaurant restaurant = restaurantDao.find(id);
        if(restaurant.isValid()) {
            RestaurantDraft draft = restaurant.getDraft();
            if(draft != null) {
                copyInfo(info, draft);
            } else {
                draft = new RestaurantDraft();
                draft.setRestaurant(restaurant);
                copyInfo(info, draft);
                restaurant.setDraft(draft);
            }
        } else {
            copyInfo(info, restaurant);
        }
    }

    @Override @Transactional
    public void publishMeal(String id, Meal meal) {
        Restaurant restaurant = restaurantDao.find(id);
        if(restaurant.isValid()) {
            restaurant.getMeals().add(copyMeal(restaurant, meal));
        } else {
            throw new InvalidActionException("This restaurant cannot publish meals: " + id);
        }
    }

    @Override @Transactional
    public void publishPromotion(String id, Promotion promotion) {
        Restaurant restaurant = restaurantDao.find(id);
        if(restaurant.isValid()) {
            boolean alreadyExist = restaurant.getPromotions().stream()
                    .anyMatch(p -> p.getAmount() == promotion.getAmount());
            if (alreadyExist) {
                throw new IllegalArgumentException("The promotion already exists: " + promotion.getAmount());
            } else {
                restaurant.getPromotions().add(copyPromotion(restaurant, promotion));
            }
        } else {
            throw new InvalidActionException("This restaurant cannot publish promotions: " + id);
        }
    }

    @Override @Transactional(readOnly = true)
    public Restaurant get(String id) {
        Restaurant restaurant = restaurantDao.find(id);
        Hibernate.initialize(restaurant.getMeals());
        Hibernate.initialize(restaurant.getPromotions());
        return restaurant;
    }

    @Override @Transactional(readOnly = true)
    public JSONObject getPublicInfo(String id) {
        Restaurant restaurant = restaurantDao.find(id);
        Hibernate.initialize(restaurant.getPromotions());
        JSONObject info = new JSONObject()
                .fluentPut("id", restaurant.getId())
                .fluentPut("name", restaurant.getName())
                .fluentPut("description", restaurant.getDescription())
                .fluentPut("type", restaurant.getType())
                .fluentPut("address", restaurant.getAddress())
                .fluentPut("phone", restaurant.getPhone())
                .fluentPut("promotions", restaurant.getPromotions());
        final Date today = new Date(System.currentTimeMillis());
        List<JSONObject> meals = restaurant.getMeals().stream()
                .filter(m -> !(m.getStartDate().after(today) || m.getEndDate().before(today)))
                .map(m -> new JSONObject()
                        .fluentPut("id", m.getId())
                        .fluentPut("name", m.getName())
                        .fluentPut("price", m.getPrice())
                        .fluentPut("description", m.getDescription())
                        .fluentPut("type", m.getType())
                        .fluentPut("num", m.getNum() - orderDao.countMeal(m.getId(), today)))
                .collect(Collectors.toList());
        info.fluentPut("meals", meals);
        return info;
    }

    @Override @Transactional(readOnly = true)
    public List<Transaction> getTransactions(TransactionCriteria criteria) {
        return transactionDao.find(criteria);
    }

    // --------------------------- private methods -----------------------------------

    private void copyInfo(RestaurantDraft info, Restaurant restaurant) {
        restaurant.setName(info.getName());
        restaurant.setType(info.getType());
        restaurant.setDescription(info.getDescription());
        restaurant.setAddress(info.getAddress());
        restaurant.setLatitude(info.getLatitude());
        restaurant.setLongitude(info.getLongitude());
        restaurant.setPhone(info.getPhone());
    }

    private void copyInfo(RestaurantDraft info, RestaurantDraft draft) {
        draft.setName(info.getName());
        draft.setType(info.getType());
        draft.setDescription(info.getDescription());
        draft.setAddress(info.getAddress());
        draft.setLatitude(info.getLatitude());
        draft.setLongitude(info.getLongitude());
        draft.setPhone(info.getPhone());
    }

    private Meal copyMeal(Restaurant restaurant, Meal old) {
        Meal meal = new Meal();
        meal.setRestaurant(restaurant);
        meal.setName(old.getName());
        meal.setType(old.getType());
        meal.setDescription(old.getDescription());
        meal.setPrice(old.getPrice());
        meal.setNum(old.getNum());
        meal.setStartDate(old.getStartDate());
        meal.setEndDate(old.getEndDate());
        return meal;
    }

    private Promotion copyPromotion(Restaurant restaurant, Promotion old) {
        Promotion promotion = new Promotion();
        promotion.setRestaurant(restaurant);
        promotion.setAmount(old.getAmount());
        promotion.setDiscount(old.getDiscount());
        promotion.setStartDate(old.getStartDate());
        promotion.setEndDate(old.getEndDate());
        return promotion;
    }
}
