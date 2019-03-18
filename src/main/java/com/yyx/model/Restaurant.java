package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yyx.util.DistanceUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "restaurant")
@NamedQueries({
        @NamedQuery(name = "Restaurant.count", query = "select count(id) from Restaurant "),
        @NamedQuery(name = "Restaurant.getInvalid", query = "from Restaurant where valid = 0"),
        @NamedQuery(name = "Restaurant.getInvalidByName",
                query = "from Restaurant where valid = 0 and name like :name"),
        @NamedQuery(name = "Restaurant.getDrafted", query = "select r from Restaurant r join r.draft"),
        @NamedQuery(name = "Restaurant.getDraftedByName",
                query = "select r from Restaurant r join r.draft where r.name like :name"),
        @NamedQuery(name = "Restaurant.getByDistance",
        query = "from Restaurant where (" + DistanceUtil.EARTH_R + " * acos(" +
                "cos(latitude * " + Math.PI + " / 180) * cos(:latitude * " + Math.PI + " / 180) * " +
                "cos((longitude - :longitude) * " + Math.PI + " / 180) + " +
                "sin(latitude * " + Math.PI + " / 180) * sin(:latitude * " + Math.PI + " / 180))) <= :distance " +
                "and valid = 1 "),
        @NamedQuery(name = "Restaurant.getByName", query = "from Restaurant where name like :name")
})
public class Restaurant implements Serializable {
    @Id
    private String id;
    @JsonIgnore
    private String pwd;
    private String name;
    private String type;
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private String phone;
    private double balance;
    private int valid;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private RestaurantDraft draft;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("amount asc")
    private List<Promotion> promotions;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<Meal> meals;

    public boolean isValid() {
        return valid == 1;
    }
}
