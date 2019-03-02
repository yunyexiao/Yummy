package com.yyx.dao.impl;

import com.yyx.dao.TransactionDao;
import com.yyx.model.Transaction;
import com.yyx.model.criteria.TransactionCriteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionDaoImpl implements TransactionDao {
    private SessionFactory factory;

    @Autowired
    public TransactionDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }
    @Override
    public void insert(Transaction transaction) {
        factory.getCurrentSession().persist(transaction);
    }

    @Override
    public Transaction findPayment(long oid) {
        return factory.getCurrentSession().createNamedQuery("Transaction.getPayment", Transaction.class)
                .setParameter("oid", oid)
                .getSingleResult();
    }

    @Override
    public List<Transaction> find(TransactionCriteria criteria) {
        return QueryHelper.transactionQuery(criteria, factory.getCurrentSession()).getResultList();
    }
}
