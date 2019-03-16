package com.yyx.dao;

import com.yyx.model.Transaction;
import com.yyx.model.criteria.TransactionCriteria;
import lombok.NonNull;

import javax.persistence.NoResultException;
import java.time.YearMonth;
import java.util.List;

public interface TransactionDao {
    void insert(@NonNull Transaction transaction);

    /**
     * @throws NoResultException if no result found
     */
    @NonNull Transaction findPayment(long oid);

    @NonNull List<Transaction> find(@NonNull TransactionCriteria criteria);

    List<String[]> getStatistics(@NonNull YearMonth start, @NonNull YearMonth end);
}
