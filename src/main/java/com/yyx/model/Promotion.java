package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Getter @Setter
@Entity
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
}
