package com.yyx.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyx.exception.AccountNotFoundException;
import com.yyx.exception.WrongPwdException;
import com.yyx.exception.customer.AlreadyConfirmedException;
import com.yyx.exception.customer.DuplicateEmailException;
import com.yyx.exception.customer.EmailKeyNotMatchException;
import com.yyx.model.Customer;
import com.yyx.model.PaymentAccount;
import com.yyx.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /**
     * @return result: 0 if successful, 1 if duplicated email.
     */
    @PostMapping("/sign-up")
    public Object signUp(@RequestParam("email") String email,
                         @RequestParam("pwd") String pwd,
                         @RequestParam("name") String name,
                         @RequestParam("phone") String phone) {
        try {
            service.signUp(email, pwd, name, phone);
            return new JSONObject().fluentPut("result", 0);
        } catch (DuplicateEmailException e) {
            return new JSONObject().fluentPut("result", 1);
        }
    }

    /**
     * @return result: 0 if successful, 1 if already confirmed, 2 if email key not match.
     */
    @GetMapping("/verify-email")
    public Object verifyEmail(@RequestParam("email") String email,
                              @RequestParam("key") String key) {
        JSONObject result = new JSONObject();
        try {
            service.confirmEmail(email, key);
            return result.fluentPut("result", 0);
        } catch (AlreadyConfirmedException e) {
            return result.fluentPut("result", 1);
        } catch (EmailKeyNotMatchException e) {
            return result.fluentPut("result", 2);
        }
    }

    /**
     * @return result: 0 if successful, 1 if account wrong, 2 if password wrong.
     */
    @PostMapping("/sign-in")
    public Object signIn(@RequestParam("email") String email,
                         @RequestParam("pwd") String pwd,
                         HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            service.signIn(email, pwd);
            HttpSession session = request.getSession(true);
            session.setAttribute("role", "customer");
            session.setAttribute("signIn", email);
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

    @PostMapping("/cancel")
    public Object cancelAccount(@SessionAttribute("signIn") String email) {
        service.cancelAccount(email);
        return new JSONObject();
    }

    @GetMapping("/info/get")
    public Object get(@SessionAttribute("signIn") String email) {
        Customer customer = service.get(email);
        return new JSONObject().fluentPut("email", email)
                .fluentPut("name", customer.getName())
                .fluentPut("phone", customer.getPhone())
                .fluentPut("level", customer.getLevel());
    }

    @PostMapping("/info/name/modify")
    public Object modifyName(@RequestParam("name") String name,
                           @SessionAttribute("signIn") String email) {
        service.modifyName(email, name);
        return new JSONObject();
    }

    @PostMapping("/info/phone/modify")
    public Object modifyPhone(@RequestParam("phone") String phone,
                            @SessionAttribute("signIn") String email) {
        service.modifyPhone(email, phone);
        return new JSONObject();
    }

    @GetMapping("/info/address/get")
    public Object getAddresses(@SessionAttribute("signIn") String email) {
        return service.get(email).getAddressInfos();
    }

    @PostMapping("/info/address/add")
    public Object addAddress(@RequestParam("address") String address,
                           @RequestParam("latitude") double latitude,
                           @RequestParam("longitude") double longitude,
                           @SessionAttribute("signIn") String email) {
        service.addAddress(email, address, latitude, longitude);
        return new JSONObject();
    }

    @PostMapping("/info/address/remove")
    public Object removeAddress(@RequestParam("aid") long aid,
                              @SessionAttribute("signIn") String email) {
        service.removeAddress(email, aid);
        return new JSONObject();
    }

    @PostMapping("/info/address/modify")
    public Object modifyAddress(@RequestParam("aid") long aid,
                              @RequestParam("address") String address,
                              @RequestParam("latitude") double latitude,
                              @RequestParam("longitude") double longitude,
                              @SessionAttribute("signIn") String email) {
        service.modifyAddress(email, aid, address, latitude, longitude);
        return new JSONObject();
    }

    @GetMapping("/info/payment/get")
    public Object getPaymentAccounts(@SessionAttribute("signIn") String email) {
        List<PaymentAccount> accounts = service.get(email).getAccounts();
        JSONArray result = new JSONArray(accounts.size());
        accounts.forEach(a -> result.add(new JSONObject(2)
                .fluentPut("pid", a.getId())
                .fluentPut("balance", a.getBalance())));
        return result;
    }

    @PostMapping("/info/payment/add")
    public Object addPaymentAccount(@RequestParam("pid") String pid,
                                  @RequestParam("pwd") String pwd,
                                  @SessionAttribute("signIn") String email) {
        service.addPaymentAccount(email, pid, pwd);
        return new JSONObject();
    }

    @PostMapping("/info/payment/remove")
    public Object removePaymentAccount(@RequestParam("pid") String pid,
                                     @SessionAttribute("signIn") String email) {
        service.removePaymentAccount(email, pid);
        return new JSONObject();
    }
}
