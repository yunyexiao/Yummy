package com.yyx.dao.impl;

import com.yyx.dao.CustomerDao;
import com.yyx.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    private SessionFactory factory;

    @Autowired
    public CustomerDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Customer find(String email) {
        Customer customer = factory.getCurrentSession().find(Customer.class, email);
        if(null != customer) {
            return customer;
        } else {
            throw new NoResultException("Customer not found: " + email);
        }
    }

    @Override
    public void insert(Customer customer) {
        factory.getCurrentSession().persist(customer);
    }

    @Override
    public void delete(String email) {
        Session session = factory.getCurrentSession();
        Customer customer = session.find(Customer.class, email);
        if(customer != null) {
            session.remove(customer);
        }
    }

    @Override
    public void update(Customer customer) {
        factory.getCurrentSession().merge(customer);
    }

    @Override
    public long count() {
        return factory.getCurrentSession().createNamedQuery("Customer.count", Long.class)
                .getSingleResult();
    }
}
