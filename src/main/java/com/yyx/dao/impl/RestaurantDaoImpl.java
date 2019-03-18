package com.yyx.dao.impl;

import com.yyx.dao.RestaurantDao;
import com.yyx.model.AddressInfo;
import com.yyx.model.Restaurant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class RestaurantDaoImpl implements RestaurantDao {
    private SessionFactory factory;

    @Autowired
    public RestaurantDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }
    @Override
    public void insert(Restaurant restaurant) {
        factory.getCurrentSession().persist(restaurant);
    }

    @Override
    public void update(Restaurant restaurant) {
        factory.getCurrentSession().merge(restaurant);
    }

    @Override
    public void delete(String id) {
        Session session = factory.getCurrentSession();
        Restaurant restaurant = session.get(Restaurant.class, id);
        if(null != restaurant) {
            session.remove(restaurant);
        }
    }

    @Override
    public Restaurant find(String id) {
        Restaurant restaurant = factory.getCurrentSession().find(Restaurant.class, id);
        if(null != restaurant) {
            return restaurant;
        } else {
            throw new NoResultException("Restaurant not found: " + id);
        }
    }

    @Override
    public List<Restaurant> findByName(String name, int pageStart, int pageSize) {
        return factory.getCurrentSession().createNamedQuery("Restaurant.getByName", Restaurant.class)
                .setParameter("name", "%" + name + "%")
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Restaurant> find(AddressInfo addressInfo, double distance, int pageStart, int pageSize) {
        return factory.getCurrentSession().createNamedQuery("Restaurant.getByDistance", Restaurant.class)
                .setParameter("latitude", addressInfo.getLatitude())
                .setParameter("longitude", addressInfo.getLongitude())
                .setParameter("distance", distance)
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Restaurant> findInvalid(int pageStart, int pageSize) {
        return factory.getCurrentSession().createNamedQuery("Restaurant.getInvalid", Restaurant.class)
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Restaurant> findInvalidByName(String name, int pageStart, int pageSize) {
        return factory.getCurrentSession().createNamedQuery("Restaurant.getInvalidByName", Restaurant.class)
                .setParameter("name", "%" + name + "%")
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Restaurant> findDrafted(int pageStart, int pageSize) {
        return factory.getCurrentSession().createNamedQuery("Restaurant.getDrafted", Restaurant.class)
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Restaurant> findDraftedByName(String name, int pageStart, int pageSize) {
        return factory.getCurrentSession().createNamedQuery("Restaurant.getDraftedByName", Restaurant.class)
                .setParameter("name", "%" + name + "%")
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long count() {
        return factory.getCurrentSession().createNamedQuery("Restaurant.count", Long.class)
                .getSingleResult();
    }
}
