package com.yyx.dao.impl;

import com.yyx.dao.PropertyDao;
import com.yyx.model.Property;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class PropertyDaoImpl implements PropertyDao {
    private SessionFactory factory;

    @Autowired
    public PropertyDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void insert(Property property) {
        factory.getCurrentSession().persist(property);
    }

    @Override
    public void update(Property property) {
        factory.getCurrentSession().merge(property);
    }

    @Override
    public Property find(String name) {
        Property property = factory.getCurrentSession().find(Property.class, name);
        if(null != property) {
            return property;
        } else {
            throw new NoResultException("Property not found: " + name);
        }
    }
}
