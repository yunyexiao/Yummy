package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
@Entity @IdClass(OrderItem.OrderItemId.class)
@Table(name = "order_item")
@NamedQueries({
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

    @Getter @Setter
    public static class OrderItemId implements Serializable {
        private Order ord;
        private Meal meal;

        @Override
        public boolean equals(Object o) {
            if (o == null)
                throw new NullPointerException();
            if (o == this)
                return true;
            if (o instanceof OrderItemId) {
                OrderItemId itemId = (OrderItemId) o;
                return this.ord.equals(itemId.ord) && this.meal.equals(itemId.meal);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ord, meal);
        }
    }
}
