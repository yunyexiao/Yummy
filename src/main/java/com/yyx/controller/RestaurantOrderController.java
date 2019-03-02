package com.yyx.controller;

import com.alibaba.fastjson.JSONObject;
import com.yyx.model.criteria.OrderCriteria;
import com.yyx.service.RestaurantOrderService;
import com.yyx.util.ModelShower;
import com.yyx.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/restaurant/order")
public class RestaurantOrderController {
    private static final Role ROLE = Role.RESTAURANT;
    private RestaurantOrderService service;

    @Autowired
    public RestaurantOrderController(RestaurantOrderService service) {
        this.service = service;
    }

    @GetMapping("/list/new")
    public Object getUnacceptedOrders(@SessionAttribute("signIn") String rid,
                                      @RequestParam("pageStart") int pageStart,
                                      @RequestParam("pageSize") int pageSize) {
        return ModelShower.filterOrders(service.getUnaccepted(rid, pageStart, pageSize), ROLE);
    }

    @GetMapping("/list/undelivered")
    public Object getUndeliveredOrders(@SessionAttribute("signIn") String rid,
                                       @RequestParam("pageStart") int pageStart,
                                       @RequestParam("pageSize") int pageSize) {
        return ModelShower.filterOrders(service.getUndelivered(rid, pageStart, pageSize), ROLE);
    }

    @GetMapping("/get/{oid}")
    public Object get(@SessionAttribute("signIn") String rid,
                      @PathVariable("oid") long oid) {
        return ModelShower.filterOrderInfo(service.get(rid, oid), ROLE);
    }

    @PostMapping("/accept/{oid}")
    public Object acceptOrder(@SessionAttribute("signIn") String rid,
                              @PathVariable("oid") long oid) {
        service.accept(rid, oid);
        return new JSONObject();
    }

    @PostMapping("/deliver/{oid}")
    public Object deliverOrder(@SessionAttribute("signIn") String rid,
                               @PathVariable("oid") long oid) {
        service.deliver(rid, oid);
        return new JSONObject();
    }

    @GetMapping("/list/delivery")
    public Object getDeliveryRecords(@SessionAttribute("signIn") String rid,
                                     @RequestParam("pageStart") int pageStart,
                                     @RequestParam("pageSize") int pageSize) {
        return ModelShower.filterOrders(service.getDeliveryRecords(rid, pageStart, pageSize), ROLE);
    }

    @GetMapping("/list/accepted")
    public Object getAcceptedOrders(@SessionAttribute("signIn") String rid,
                                    @RequestParam(name = "startTime", required = false) String startTime,
                                    @RequestParam(name = "endTime", required = false) String endTime,
                                    @RequestParam(name = "costBottom", required = false) Double costBottom,
                                    @RequestParam(name = "costTop", required = false) Double costTop,
                                    @RequestParam("groupByCustomer") boolean groupByCustomer,
                                    @RequestParam("pageStart") int pageStart,
                                    @RequestParam("pageSize") int pageSize) {
        OrderCriteria criteria = generateCriteria(rid, startTime, endTime, costBottom,
                costTop, groupByCustomer, pageStart, pageSize);
        return ModelShower.filterOrders(service.getAccepted(criteria), ROLE);
    }

    @GetMapping("/list/cancelled")
    public Object getCancelledOrders(@SessionAttribute("signIn") String rid,
                                     @RequestParam(name = "startTime", required = false) String startTime,
                                     @RequestParam(name = "endTime", required = false) String endTime,
                                     @RequestParam(name = "costBottom", required = false) Double costBottom,
                                     @RequestParam(name = "costTop", required = false) Double costTop,
                                     @RequestParam("groupByCustomer") boolean groupByCustomer,
                                     @RequestParam("pageStart") int pageStart,
                                     @RequestParam("pageSize") int pageSize) {
        OrderCriteria criteria = generateCriteria(rid, startTime, endTime, costBottom,
                costTop, groupByCustomer, pageStart, pageSize);
        return ModelShower.filterOrders(service.getCancelled(criteria), ROLE);
    }

    private OrderCriteria generateCriteria(String rid, String startTime, String endTime, Double costBottom,
                                           Double costTop, boolean groupByCustomer, int pageStart, int pageSize) {
        OrderCriteria criteria = new OrderCriteria();
        criteria.setRid(rid);
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
        criteria.setGrouped(groupByCustomer);
        criteria.setPageStart(pageStart);
        criteria.setPageSize(pageSize);
        return criteria;
    }
}
