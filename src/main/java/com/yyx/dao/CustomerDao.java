package com.yyx.dao;

import com.yyx.model.Customer;
import lombok.NonNull;

import javax.persistence.NoResultException;

public interface CustomerDao {
    /**
     * @throws NoResultException if no result found
     */
    @NonNull Customer find(@NonNull String email);

    void insert(@NonNull Customer customer);

    void delete(@NonNull String email);

    void update(@NonNull Customer customer);

    long count();
}
