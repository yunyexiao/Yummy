package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter @Setter
@Entity
@Table(name = "order_item")
@NamedQueries({
        @NamedQuery(name = "OrderItem.countMeal0",
        query = "select sum(num) from OrderItem where meal.id = :mid " +
                "and year(ord.placeTime) = year(:date) " +
                "and month(ord.placeTime) = month(:date) " +
                "and day(ord.placeTime) = day(:date)"),
        @NamedQuery(name = "OrderItem.countMeal",
        query = "select sum(num) from OrderItem where meal.id = :mid and date(ord.placeTime) = date(:date)")
})
public class OrderItem implements Serializable {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "oid")
    @JsonIgnore
    private Order ord;
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "mid")
    private Meal meal;
    private int num;
}
