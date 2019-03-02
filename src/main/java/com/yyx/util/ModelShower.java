package com.yyx.util;

import com.alibaba.fastjson.JSONObject;
import com.yyx.model.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public final class ModelShower {
    private static final SimpleDateFormat TIMESTAMP_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd");

    private ModelShower() {
        // empty constructor
    }

    public static JSONObject filterOrderInfo(Order order, Role role) {
        JSONObject info = filterOrderBrief(order, role);
        List<JSONObject> items = order.getItems().stream()
                .map(i -> new JSONObject(3)
                        .fluentPut("mid", i.getMeal().getId())
                        .fluentPut("name", i.getMeal().getName())
                        .fluentPut("num", i.getNum()))
                .collect(Collectors.toList());
        info.fluentPut("items", items);
        switch (role) {
            case RESTAURANT:
                info.fluentPut("restaurant", filterRestaurantInfo(order.getRestaurant()));
                break;
            case CUSTOMER:
                info.fluentPut("customer", filterCustomerInfo(order.getCustomer()));
                break;
        }
        return info;
    }

    public static List<JSONObject> filterOrders(List<Order> orders, Role role) {
        return orders.stream().map(o -> filterOrderBrief(o, role)).collect(Collectors.toList());
    }

    public static List<JSONObject> convertMeals(List<Meal> meals) {
        return meals.stream().map(ModelShower::convertMeal).collect(Collectors.toList());
    }

    public static List<JSONObject> convertPromotions(List<Promotion> promotions) {
        return promotions.stream().map(ModelShower::convertPromotion).collect(Collectors.toList());
    }

    public static List<JSONObject> convertTransactions(List<Transaction> transactions) {
        return transactions.stream().map(ModelShower::convertTransaction)
                .collect(Collectors.toList());
    }

    public static JSONObject convertRestaurant(Restaurant restaurant) {
        return convertRestaurantListTile(restaurant)
                .fluentPut("latitude", restaurant.getLatitude())
                .fluentPut("longitude", restaurant.getLongitude())
                .fluentPut("phone", restaurant.getPhone());
    }

    public static List<JSONObject> convertRestaurants(List<Restaurant> restaurants) {
        return restaurants.stream().map(ModelShower::convertRestaurantListTile)
                .collect(Collectors.toList());
    }

    private static JSONObject convertRestaurantListTile(Restaurant restaurant) {
        return new JSONObject(5)
                .fluentPut("id", restaurant.getId())
                .fluentPut("name", restaurant.getName())
                .fluentPut("type", restaurant.getType())
                .fluentPut("description", restaurant.getDescription())
                .fluentPut("address", restaurant.getAddress());
    }

    private static JSONObject convertMeal(Meal meal) {
        return new JSONObject(7)
                .fluentPut("id", meal.getId())
                .fluentPut("name", meal.getName())
                .fluentPut("type", meal.getType())
                .fluentPut("description", meal.getDescription())
                .fluentPut("price", meal.getPrice())
                .fluentPut("num", meal.getNum())
                .fluentPut("startDate", DATE_FORMATTER.format(meal.getStartDate()))
                .fluentPut("endDate", DATE_FORMATTER.format(meal.getEndDate()));
    }

    private static JSONObject convertPromotion(Promotion promotion) {
        return new JSONObject(4)
                .fluentPut("amount", promotion.getAmount())
                .fluentPut("discount", promotion.getDiscount())
                .fluentPut("startDate", DATE_FORMATTER.format(promotion.getStartDate()))
                .fluentPut("endDate", DATE_FORMATTER.format(promotion.getEndDate()));
    }

    private static JSONObject convertTransaction(Transaction transaction) {
        return new JSONObject(4)
                .fluentPut("id", transaction.getId())
                .fluentPut("oid", transaction.getOrder().getId())
                .fluentPut("amount", transaction.getAmount())
                .fluentPut("time", TIMESTAMP_FORMATTER.format(transaction.getTime()));
    }

    private static JSONObject filterOrderBrief(Order order, Role role) {
        JSONObject info = new JSONObject()
                .fluentPut("id", order.getId())
                .fluentPut("destination", order.getDestination().getAddress())
                .fluentPut("cost", order.getCost())
                .fluentPut("state", order.getState())
                .fluentPut("placeTime", TIMESTAMP_FORMATTER.format(order.getPlaceTime()));
        switch (role) {
            case CUSTOMER:
                info.fluentPut("restaurant", filterRestaurantInfo(order.getRestaurant()));
                break;
            case RESTAURANT:
                info.fluentPut("customer", filterCustomerInfo(order.getCustomer()));
                break;
            case ADMIN:
                info.fluentPut("customer", order.getCustomer().getEmail())
                        .fluentPut("restaurant", order.getRestaurant().getId());
                break;
        }
        if (order.getCompleteTime() != null) {
            info.fluentPut("completeTime", TIMESTAMP_FORMATTER.format(order.getCompleteTime()));
        } else if (order.getCancelTime() != null) {
            info.fluentPut("cancelTime", TIMESTAMP_FORMATTER.format(order.getCancelTime()));
        }
        return info;
    }

    private static JSONObject filterCustomerInfo(Customer customer) {
        return new JSONObject(3)
                .fluentPut("email", customer.getEmail())
                .fluentPut("name", customer.getName())
                .fluentPut("phone", customer.getPhone());
    }

    private static JSONObject filterRestaurantInfo(Restaurant restaurant) {
        return new JSONObject()
                .fluentPut("id", restaurant.getId())
                .fluentPut("name", restaurant.getName())
                .fluentPut("address", restaurant.getAddress())
                .fluentPut("phone", restaurant.getPhone());
    }
}
