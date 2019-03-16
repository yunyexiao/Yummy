package com.yyx.dao.impl;

import com.yyx.dao.TransactionDao;
import com.yyx.model.Transaction;
import com.yyx.model.criteria.TransactionCriteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final String getStatisticsSqlString = "select year(`time`) as `year`, month(`time`) as `month`, " +
                "if(outAccount = 'YUMMY', 'outcome', 'income') as `type`, sum(amount) as amount " +
                "from `transaction` where `time` between :start and :end " +
                "group by `year`, `month`, `type` order by `year`, `month`, `type`";

    @Override
    public List<String[]> getStatistics(YearMonth start, YearMonth end) {
        String startString = start.toString() + "-01 00:00:00.000";
        String endString = end.toString() + '-' + end.lengthOfMonth() + " 23:59:59.999";
        List<?> rawData = (List<?>) factory.getCurrentSession().createSQLQuery(getStatisticsSqlString)
                .setParameter("start", startString)
                .setParameter("end", endString)
                .getResultList();
        return rawData.stream().map(row -> {
            Object[] data = (Object[]) row;
            String[] stringData = new String[data.length];
            for (int i = 0; i < data.length; i++) stringData[i] = data[i].toString();
            return stringData;
        }).collect(Collectors.toList());
    }
}
