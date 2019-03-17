package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter @Setter
@Entity
@Table(name = "restaurant_draft")
public class RestaurantDraft implements Serializable {
    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "id")
    @JsonIgnore
    private Restaurant restaurant;
    private String name;
    private String type;
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (o == null)
            throw new NullPointerException();
        if (o == this)
            return true;
        if (o instanceof RestaurantDraft) {
            RestaurantDraft draft = (RestaurantDraft) o;
            return draft.restaurant.equals(restaurant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 + restaurant.hashCode();
    }
}
