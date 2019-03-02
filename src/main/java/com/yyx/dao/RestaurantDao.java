package com.yyx.dao;

import com.yyx.model.AddressInfo;
import com.yyx.model.Restaurant;
import lombok.NonNull;

import javax.persistence.NoResultException;
import java.util.List;

public interface RestaurantDao {
    void insert(@NonNull Restaurant restaurant);

    void update(@NonNull Restaurant restaurant);

    void delete(@NonNull String id);

    /**
     * @throws NoResultException if no result found
     */
    @NonNull Restaurant find(@NonNull String id);

    /**
     * Find restaurants by distance off the given address. All result restaurants are valid.
     */
    @NonNull List<Restaurant> find(@NonNull AddressInfo addressInfo, double distance, int pageStart, int pageCount);

    @NonNull List<Restaurant> findInvalid(int pageStart, int pageSize);

    @NonNull List<Restaurant> findDrafted(int pageStart, int pageSize);

    long count();
}
