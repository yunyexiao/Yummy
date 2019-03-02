package com.yyx.controller;

import com.alibaba.fastjson.JSONObject;
import com.yyx.exception.WrongPwdException;
import com.yyx.model.Restaurant;
import com.yyx.service.AdminService;
import com.yyx.util.ModelShower;
import com.yyx.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Role ROLE = Role.ADMIN;
    private AdminService service;

    @Autowired
    public AdminController(AdminService service) {
        this.service = service;
    }

    /**
     * @return 0 for success, 1 for wrong password
     */
    @PostMapping("/sign-in")
    public Object signIn(@RequestParam("pwd") String pwd, HttpServletRequest request) {
        JSONObject result = new JSONObject(1);
        try {
            service.signIn(pwd);
            HttpSession session = request.getSession(true);
            session.setAttribute("role", "admin");
            return result.fluentPut("result", 0);
        } catch (WrongPwdException e) {
            return result.fluentPut("result", 1);
        }
    }

    @PostMapping("/sign-out")
    public Object signOut(HttpSession session) {
        session.invalidate();
        return new JSONObject();
    }

    @GetMapping("/list/order/unsettled")
    public Object getUnsettledOrders(@RequestParam("pageStart") int pageStart,
                                     @RequestParam("pageSize") int pageSize) {
        return ModelShower.filterOrders(service.getUnsettledOrders(pageStart, pageSize), ROLE);
    }

    @PostMapping("/settle/{oid}")
    public Object settleOrder(@PathVariable("oid") long oid) {
        service.settleOrder(oid);
        return new JSONObject(0);
    }

    @GetMapping("/list/restaurant/drafted")
    public Object getDraftedRestaurants(@RequestParam("pageStart") int pageStart,
                                        @RequestParam("pageSize") int pageSize) {
        List<Restaurant> restaurants = service.getDraftedRestaurants(pageStart, pageSize);
        return ModelShower.convertRestaurants(restaurants);
    }

    @GetMapping("/list/restaurant/invalid")
    public Object getInvalidRestaurants(@RequestParam("pageStart") int pageStart,
                                        @RequestParam("pageSize") int pageSize) {
        List<Restaurant> restaurants = service.getInvalidRestaurants(pageStart, pageSize);
        return ModelShower.convertRestaurants(restaurants);
    }

    @GetMapping("/get/restaurant/{rid}")
    public Object getRestaurantInfo(@PathVariable("rid") String rid) {
        Restaurant restaurant = service.getUncheckedRestaurant(rid);
        return ModelShower.convertRestaurant(restaurant)
                .fluentPut("draft", restaurant.getDraft());
    }

    @PostMapping("/check/{rid}")
    public Object checkRestaurant(@PathVariable("rid") String rid,
                                  @RequestParam("approved") boolean approved) {
        service.checkRestaurant(rid, approved);
        return new JSONObject(0);
    }

    @GetMapping("/statistics")
    public Object getStatistics() {
        return new JSONObject(2)
                .fluentPut("customerCount", service.getCustomerCount())
                .fluentPut("restaurantCount", service.getRestaurantCount());
    }

    @GetMapping("/finance")
    public Object getFinancialAffairs(@RequestParam("start") String start,
                                      @RequestParam("end") String end) {
        return service.getFinancialAffairs(YearMonth.parse(start), YearMonth.parse(end));
    }

}
