package com.yyx.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyx.exception.AccountNotFoundException;
import com.yyx.exception.InvalidActionException;
import com.yyx.exception.WrongPwdException;
import com.yyx.model.Meal;
import com.yyx.model.Promotion;
import com.yyx.model.Restaurant;
import com.yyx.model.RestaurantDraft;
import com.yyx.model.criteria.TransactionCriteria;
import com.yyx.service.RestaurantService;
import com.yyx.util.ModelShower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
    private RestaurantService service;

    @Autowired
    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping("/get/{id}")
    public Object getPublicInfo(@PathVariable("id") String id) {
        return service.getPublicInfo(id);
    }

    @GetMapping("/search")
    public Object searchByName(@RequestParam("name") String name,
                               @RequestParam("pageStart") int pageStart,
                               @RequestParam("pageSize") int pageSize) {
        return ModelShower.convertRestaurants(service.searchByName(name, pageStart, pageSize));
    }

    @PostMapping("/sign-up")
    public Object signUp(@RequestBody JSONObject infoJson) {
        String id = service.signUp(toDraft(infoJson), infoJson.getString("pwd"));
        return new JSONObject().fluentPut("id", id);
    }

    /**
     * @return 0 for success, 1 for account not found, 2 for wrong password
     */
    @PostMapping("/sign-in")
    public Object signIn(@RequestParam("id") String id,
                         @RequestParam("pwd") String pwd,
                         HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            service.signIn(id, pwd);
            HttpSession session = request.getSession(true);
            session.setAttribute("role", "restaurant");
            session.setAttribute("signIn", id);
            return result.fluentPut("result", 0);
        } catch (AccountNotFoundException e) {
            return result.fluentPut("result", 1);
        } catch (WrongPwdException e) {
            return result.fluentPut("result", 2);
        }
    }

    @PostMapping("/sign-out")
    public Object signOut(HttpSession session) {
        session.invalidate();
        return new JSONObject();
    }

    @GetMapping("/info/get")
    public Object getInfo(@SessionAttribute("signIn") String id) {
        Restaurant restaurant = service.get(id);
        return new JSONObject()
                .fluentPut("id", restaurant.getId())
                .fluentPut("name", restaurant.getName())
                .fluentPut("type", restaurant.getType())
                .fluentPut("description", restaurant.getDescription())
                .fluentPut("address", restaurant.getAddress())
                .fluentPut("latitude", restaurant.getLatitude())
                .fluentPut("longitude", restaurant.getLongitude())
                .fluentPut("phone", restaurant.getPhone())
                .fluentPut("balance", restaurant.getBalance())
                .fluentPut("valid", restaurant.getValid());
    }

    @GetMapping("/info/draft")
    public Object getDraft(@SessionAttribute("signIn") String id) {
        RestaurantDraft draft = service.get(id).getDraft();
        return draft == null ? "" : draft;
    }

    @PostMapping("/info/modify")
    public Object modifyInfo(@SessionAttribute("signIn") String id,
                             @RequestBody JSONObject infoJson) {
        service.modifyInfo(id, toDraft(infoJson));
        return new JSONObject();
    }

    /**
     * @return 0 for success, 1 for invalid action
     */
    @PostMapping("/meal/publish")
    public Object publishMeals(@SessionAttribute("signIn") String id,
                               @RequestBody JSONObject mealJson) {
        JSONObject result = new JSONObject(1);
        try {
            Meal meal = mealJson.toJavaObject(Meal.class);
            service.publishMeal(id, meal);
            return result.fluentPut("result", 0);
        } catch (InvalidActionException e) {
            return result.fluentPut("result", 1);
        }
    }

    @GetMapping("/meal/get")
    public Object getMeals(@SessionAttribute("signIn") String id) {
        return ModelShower.convertMeals(service.get(id).getMeals());
    }

    /**
     * @return 0 for success, 1 for invalid action
     */
    @PostMapping("/promotion/publish")
    public Object publishPromotions(@SessionAttribute("signIn") String id,
                                    @RequestBody JSONObject promotionJson) {
        JSONObject result = new JSONObject(1);
        try {
            Promotion promotion = promotionJson.toJavaObject(Promotion.class);
            service.publishPromotion(id, promotion);
            return result.fluentPut("result", 0);
        } catch (InvalidActionException e) {
            return result.fluentPut("result", 1);
        }
    }

    @GetMapping("/promotion/get")
    public Object getPromotions(@SessionAttribute("signIn") String id) {
        return ModelShower.convertPromotions(service.get(id).getPromotions());
    }

    @GetMapping("/finance/list")
    public Object getTransactions(@SessionAttribute("signIn") String id,
                                  @RequestParam("startDate") String startDate,
                                  @RequestParam("endDate") String endDate,
                                  @RequestParam("pageStart") int pageStart,
                                  @RequestParam("pageSize") int pageSize) {
        TransactionCriteria criteria = TransactionCriteria.builder()
                .account(id)
                .startTime(Timestamp.valueOf(startDate + " 00:00:00.000"))
                .endTime(Timestamp.valueOf(endDate + " 23:59:59.999"))
                .pageStart(pageStart)
                .pageSize(pageSize)
                .build();
        return ModelShower.convertTransactions(service.getTransactions(criteria));
    }

    private RestaurantDraft toDraft(JSONObject info) {
        RestaurantDraft draft = new RestaurantDraft();
        draft.setName(info.getString("name"));
        draft.setDescription(info.getString("description"));
        draft.setType(info.getString("type"));
        draft.setAddress(info.getString("address"));
        draft.setLatitude(info.getDoubleValue("latitude"));
        draft.setLongitude(info.getDoubleValue("longitude"));
        draft.setPhone(info.getString("phone"));
        return draft;
    }

}
