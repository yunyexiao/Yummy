package com.yyx.service;

import com.alibaba.fastjson.JSONObject;
import com.yyx.exception.*;
import com.yyx.model.*;
import com.yyx.model.criteria.TransactionCriteria;

import java.util.List;

public interface RestaurantService {
    /**
     * @param info no need for restaurant attribute
     *
     * @return the new generated id
     */
    String signUp(RestaurantDraft info, String pwd);

    /**
     * @throws AccountNotFoundException if no such account
     * @throws WrongPwdException if password is wrong
     */
    void signIn(String id, String pwd);

    /**
     * @param info no need for restaurant attribute
     */
    void modifyInfo(String id, RestaurantDraft info);

    /**
     * @param meal no need for id and restaurant attributes
     *
     * @throws InvalidActionException if the restaurant is invalid
     */
    void publishMeal(String id, Meal meal);

    /**
     * @param promotion no need for restaurant attribute
     *
     * @throws InvalidActionException if the restaurant is invalid
     */
    void publishPromotion(String id, Promotion promotion);

    Restaurant get(String id);

    JSONObject getPublicInfo(String id);

    List<Transaction> getTransactions(TransactionCriteria criteria);
}
