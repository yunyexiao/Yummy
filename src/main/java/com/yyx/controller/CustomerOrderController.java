package com.yyx.controller;

import com.alibaba.fastjson.JSONObject;
import com.yyx.exception.WrongPwdException;
import com.yyx.exception.order.MealSoldOutException;
import com.yyx.exception.order.OverspentException;
import com.yyx.model.Restaurant;
import com.yyx.model.criteria.OrderCriteria;
import com.yyx.service.CustomerOrderService;
import com.yyx.util.ModelShower;
import com.yyx.util.Role;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/order")
public class CustomerOrderController {
    private static final Role ROLE = Role.CUSTOMER;
    private CustomerOrderService service;

    @Autowired
    public CustomerOrderController(CustomerOrderService service) {
        this.service = service;
    }

    @GetMapping("/restaurants")
    public Object getRestaurantList(@SessionAttribute("signIn") String email,
                                    @RequestParam("aid") long aid,
                                    @RequestParam("distance") double distance,
                                    @RequestParam("pageStart") int pageStart,
                                    @RequestParam("pageSize") int pageSize,
                                    HttpSession session) {
        session.setAttribute("address", aid);
        List<Restaurant> restaurants = service.getRestaurants(email, aid, distance, pageStart, pageSize);
        return ModelShower.convertRestaurants(restaurants);
    }

    @GetMapping("/estimate-time")
    public Object estimateOrderTime(@SessionAttribute("signIn") String email,
                                    @SessionAttribute("address") long aid,
                                    @RequestParam("rid") String rid) {
        int time = service.estimateDeliveryTime(email, aid, rid);
        return new JSONObject().fluentPut("time", time);
    }

    @PostMapping("/cost/calc")
    public Object calcCost(@SessionAttribute("signIn") String email,
                           @RequestParam("rid") String rid,
                           @RequestBody Map<Long, Integer> items) {
        return new JSONObject().fluentPut("cost", service.calcCost(email, rid, items));
    }

    /**
     * @return 0 for success, 1 for meal sold out.
     */
    @PostMapping("/place")
    public Object placeOrder(@SessionAttribute("signIn") String email,
                             @RequestParam("rid") String rid,
                             @SessionAttribute("address") long aid,
                             @RequestBody Map<Long, Integer> items) {
        JSONObject result = new JSONObject();
        try {
            service.place(email, rid, aid, items);
            return result.fluentPut("result", 0);
        } catch (MealSoldOutException e) {
            return result.fluentPut("result", 1);
        }
    }

    @GetMapping("/list/placed")
    public Object getPlacedOrders(@SessionAttribute("signIn") String email,
                                  @RequestParam("pageStart") int pageStart,
                                  @RequestParam("pageSize") int pageSize) {
        return ModelShower.filterOrders(service.getPlaced(email, pageStart, pageSize), ROLE);
    }

    @GetMapping("/get/{oid}")
    public Object getOrder(@SessionAttribute("signIn") String email,
                           @PathVariable("oid") long oid) {
        return ModelShower.filterOrderInfo(service.get(email, oid), ROLE);
    }

    /**
     * @return 0 for success, 1 for wrong password, 2 for balance not enough
     */
    @PostMapping("/pay/{oid}")
    public Object payOrder(@SessionAttribute("signIn") String email,
                           @PathVariable("oid") long oid,
                           @RequestParam("pid") String pid,
                           @RequestParam("pwd") String pwd) {
        JSONObject result = new JSONObject();
        try {
            service.pay(email, oid, pid, pwd);
            return result.fluentPut("result", 0);
        } catch (WrongPwdException e) {
            return result.fluentPut("result", 1);
        } catch (OverspentException e) {
            return result.fluentPut("result", 2);
        }
    }

    @GetMapping("/list/waiting")
    public Object getPayedNotCompletedOrders(@SessionAttribute("signIn") String email,
                                             @RequestParam("pageStart") int pageStart,
                                             @RequestParam("pageSize") int pageSize) {
        return ModelShower.filterOrders(service.getPayedNotCompleted(email, pageStart, pageSize), ROLE);
    }

    @PostMapping("/complete/{oid}")
    public Object completeOrder(@SessionAttribute("signIn") String email,
                                @PathVariable("oid") long oid) {
        service.complete(email, oid);
        return new JSONObject();
    }

    /**
     * @return 0 for success, 1 for failure
     */
    @PostMapping("/cancel/{oid}")
    public Object cancelOrder(@SessionAttribute("signIn") String email,
                              @PathVariable("oid") long oid) {
        JSONObject result = new JSONObject();
        if(service.cancel(email, oid)) {
            return result.fluentPut("result", 0);
        } else {
            return result.fluentPut("result", 1);
        }
    }

    @GetMapping("/list/all")
    public Object getAllOrders(@SessionAttribute("signIn") String email,
                               @RequestParam(name = "startTime", required = false) String startTime,
                               @RequestParam(name = "endTime", required = false) String endTime,
                               @RequestParam(name = "costBottom", required = false) Double costBottom,
                               @RequestParam(name = "costTop", required = false) Double costTop,
                               @RequestParam("groupByRestaurant") boolean groupByRestaurant,
                               @RequestParam("pageStart") int pageStart,
                               @RequestParam("pageSize") int pageSize) {
        OrderCriteria criteria = generateCriteria(email, startTime, endTime, costBottom,
                costTop, groupByRestaurant, pageStart, pageSize);
        return ModelShower.filterOrders(service.getAll(criteria), ROLE);
    }

    @GetMapping("/list/payed")
    public Object getPayedOrders(@SessionAttribute("signIn") String email,
                                 @RequestParam(name = "startTime", required = false) String startTime,
                                 @RequestParam(name = "endTime", required = false) String endTime,
                                 @RequestParam(name = "costBottom", required = false) Double costBottom,
                                 @RequestParam(name = "costTop", required = false) Double costTop,
                                 @RequestParam("groupByRestaurant") boolean groupByRestaurant,
                                 @RequestParam("pageStart") int pageStart,
                                 @RequestParam("pageSize") int pageSize) {
        OrderCriteria criteria = generateCriteria(email, startTime, endTime, costBottom,
                costTop, groupByRestaurant, pageStart, pageSize);
        return ModelShower.filterOrders(service.getPayed(criteria), ROLE);
    }

    @GetMapping("/list/cancelled")
    public Object getCancelled(@SessionAttribute("signIn") String email,
                               @RequestParam(name = "startTime", required = false) String startTime,
                               @RequestParam(name = "endTime", required = false) String endTime,
                               @RequestParam(name = "costBottom", required = false) Double costBottom,
                               @RequestParam(name = "costTop", required = false) Double costTop,
                               @RequestParam("groupByRestaurant") boolean groupByRestaurant,
                               @RequestParam("pageStart") int pageStart,
                               @RequestParam("pageSize") int pageSize) {
        OrderCriteria criteria = generateCriteria(email, startTime, endTime, costBottom,
                costTop, groupByRestaurant, pageStart, pageSize);
        return ModelShower.filterOrders(service.getCancelled(criteria), ROLE);
    }

    private OrderCriteria generateCriteria(@NonNull String email, String startTime, String endTime, Double costBottom,
                                           Double costTop, boolean groupByRestaurant, int pageStart, int pageSize) {
        OrderCriteria criteria = new OrderCriteria();
        criteria.setEmail(email);
        if(startTime != null) {
            criteria.setStartTime(Timestamp.valueOf(startTime));
        }
        if(endTime != null) {
            criteria.setEndTime(Timestamp.valueOf(endTime));
        }
        if(costBottom != null) {
            criteria.setCostBottom(costBottom);
        }
        if(costTop != null) {
            criteria.setCostTop(costTop);
        }
        criteria.setGrouped(groupByRestaurant);
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return criteria;
    }
}
