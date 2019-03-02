package com.yyx.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "`order`")
@NamedQueries({
        @NamedQuery(name = "Order.sumConsumption",
        query = "select sum(cost) from Order where customer.email = :email and state = " + OrderState.COMPLETED)
})
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "email")
    private Customer customer;
    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant")
    private Restaurant restaurant;
    @ManyToOne(optional = false)
    @JoinColumn(name = "destination")
    private AddressInfo destination;
    private double cost;
    private int state;
    private Timestamp placeTime;
    private Timestamp completeTime;
    private Timestamp cancelTime;

    @OneToMany(mappedBy = "ord", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("meal asc")
    private List<OrderItem> items;
}
