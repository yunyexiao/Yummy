package com.yyx.dao;

import com.yyx.model.Property;
import lombok.NonNull;

import javax.persistence.NoResultException;

public interface PropertyDao {
    void insert(@NonNull Property property);

    void update(@NonNull Property property);

    /**
     * @throws NoResultException if no result found
     */
    @NonNull Property find(String name);
}
