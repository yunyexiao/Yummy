package com.yyx.service.impl;

import com.yyx.dao.CustomerDao;
import com.yyx.exception.AccountNotFoundException;
import com.yyx.exception.WrongPwdException;
import com.yyx.exception.customer.AlreadyConfirmedException;
import com.yyx.exception.customer.DuplicateEmailException;
import com.yyx.exception.customer.EmailKeyNotMatchException;
import com.yyx.model.*;
import com.yyx.service.CustomerService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Service
@EnableTransactionManagement
public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao;
    private ModelValidator validator;
    private EmailKeyGenerator keyGenerator;
    private EmailHelper emailHelper;

    @Autowired
    public CustomerServiceImpl(CustomerDao customerDao, ModelValidator validator,
                               EmailKeyGenerator keyGenerator, EmailHelper emailHelper) {
        this.customerDao = customerDao;
        this.validator = validator;
        this.keyGenerator = keyGenerator;
        this.emailHelper = emailHelper;
    }

    @Override @Transactional
    public void signUp(String email, String pwd, String name, String phone) {
        checkDuplicate(email);
        // create a customer
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPwd(pwd);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setLevel(0);
        customer.setValid(0);

        // generate a email-key pair
        EmailKey emailKey = new EmailKey();
        emailKey.setCustomer(customer);
        emailKey.setKey(keyGenerator.generate());
        customer.setEmailKey(emailKey);

        customerDao.insert(customer);

        // send an email to customer
        sendCheckEmail(emailKey);
    }

    @Override @Transactional(readOnly = true)
    public void signIn(String email, String pwd) {
        Customer customer;
        try {
            customer = customerDao.find(email);
        } catch (NoResultException e) {
            throw new AccountNotFoundException(email);
        }
        if(!customer.isValid()) {
            throw new AccountNotFoundException(email);
        }
        if(!customer.getPwd().equals(pwd)) {
            throw new WrongPwdException(email, pwd);
        }
    }

    @Override @Transactional
    public void confirmEmail(String email, String key) {
        Customer customer = customerDao.find(email);
        EmailKey emailKey = customer.getEmailKey();
        if(emailKey == null) {
            throw new AlreadyConfirmedException(email);
        }
        if(emailKey.getKey().equals(key)) {
            customer.setValid(1);
            customer.setEmailKey(null);
        } else {
            throw new EmailKeyNotMatchException(email, key);
        }
    }

    @Override @Transactional
    public void modifyName(String email, String name) {
        Customer customer = validator.getValidCustomer(email);
        customer.setName(name);
    }

    @Override @Transactional
    public void modifyPhone(String email, String phone) {
        Customer customer = validator.getValidCustomer(email);
        customer.setPhone(phone);
    }

    @Override @Transactional
    public void addAddress(String email, String address, double latitude, double longitude) {
        Customer customer = validator.getValidCustomer(email);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setCustomer(customer);
        addressInfo.setAddress(address);
        addressInfo.setLatitude(latitude);
        addressInfo.setLongitude(longitude);
        customer.getAddressInfos().add(addressInfo);
    }

    @Override @Transactional
    public void removeAddress(String email, long aid) {
        Customer customer = validator.getValidCustomer(email);
        customer.getAddressInfos().removeIf(a -> a.getId() == aid);
    }

    @Override @Transactional
    public void modifyAddress(String email, long aid, String address, double latitude, double longitude) {
        AddressInfo addressInfo = validator.getValidAddress(email, aid);
        addressInfo.setAddress(address);
        addressInfo.setLatitude(latitude);
        addressInfo.setLongitude(longitude);
    }

    @Override @Transactional
    public void addPaymentAccount(String email, String pid, String pwd) {
        Customer customer = validator.getValidCustomer(email);
        if(customer.getAccounts().stream().anyMatch(a -> a.getId().equals(pid))) {
            throw new IllegalArgumentException("Payment account already exists: " + pid);
        }
        PaymentAccount realAccount = new PaymentAccount();
        realAccount.setId(pid);
        realAccount.setPwd(pwd);
        realAccount.setCustomer(customer);
        realAccount.setBalance(0);
        customer.getAccounts().add(realAccount);
    }

    @Override @Transactional
    public void removePaymentAccount(String email, String pid) {
        Customer customer = validator.getValidCustomer(email);
        customer.getAccounts().removeIf(a -> a.getId().equals(pid));
    }

    @Override @Transactional
    public void cancelAccount(String email) {
        Customer customer = validator.getValidCustomer(email);
        customer.setValid(0);
    }

    @Override @Transactional(readOnly = true)
    public Customer get(String email) {
        Customer customer = validator.getValidCustomer(email);
        Hibernate.initialize(customer.getAddressInfos());
        Hibernate.initialize(customer.getAccounts());
        return customer;
    }

    // private methods

    private void checkDuplicate(String email) {
        try {
            customerDao.find(email);
            throw new DuplicateEmailException(email);
        } catch (NoResultException e) {
            // this is the right way
        }
    }

    private void sendCheckEmail(EmailKey emailKey) {
        String toId = emailKey.getCustomer().getEmail();
        String subject = "Please Check Your Account";
        String url = "http://localhost:8080/Yummy/customer/verify-email?email=" +
                toId + "&key=" + emailKey.getKey();
        String body = "Your email was used to sign up an account at our yummy platform. Please validate this " +
                "account by clicking the link below: \n" + url + "\nHave a good day.";
        emailHelper.send(toId, subject, body);
    }

}
