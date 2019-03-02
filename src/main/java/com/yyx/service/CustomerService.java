package com.yyx.service;

import com.yyx.exception.customer.*;
import com.yyx.exception.*;
import com.yyx.model.Customer;

public interface CustomerService {
    /**
     * @throws DuplicateEmailException if email already exists
     */
    void signUp(String email, String pwd, String name, String phone);

    /**
     * @throws AccountNotFoundException if no such account with given email
     * @throws WrongPwdException if password does not match email
     */
    void signIn(String email, String pwd);

    /**
     * @throws AlreadyConfirmedException if the customer already confirmed
     * @throws EmailKeyNotMatchException if email-key pair not match
     */
    void confirmEmail(String email, String key);

    void modifyName(String email, String name);

    void modifyPhone(String email, String phone);

    void addAddress(String email, String address, double latitude, double longitude);

    void removeAddress(String email, long aid);

    void modifyAddress(String email, long aid, String address, double latitude, double longitude);

    void addPaymentAccount(String email, String pid, String pwd);

    void removePaymentAccount(String email, String pid);

    void cancelAccount(String email);

    /**
     * Get customer info, addresses and payment accounts.
     */
    Customer get(String email);
}
