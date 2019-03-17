package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Getter @Setter
@Entity @IdClass(Promotion.PromotionId.class)
@Table(name = "promotion")
public class Promotion implements Serializable {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant")
    @JsonIgnore
    private Restaurant restaurant;
    @Id
    private double amount;
    private double discount;
    private Date startDate;
    private Date endDate;

    @Getter @Setter
    public static class PromotionId implements Serializable {
        private Restaurant restaurant;
        private double amount;

        @Override
        public boolean equals(Object o) {
            if (o == null)
                throw new NullPointerException();
            if (o == this)
                return true;
            if (o instanceof PromotionId) {
                PromotionId promotionId = (PromotionId) o;
                return promotionId.restaurant.equals(this.restaurant) && promotionId.amount == this.amount;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(restaurant, amount);
        }
    }
}
