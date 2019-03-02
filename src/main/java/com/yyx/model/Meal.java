package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Getter @Setter
@Entity
@Table(name = "meal")
public class Meal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant")
    @JsonIgnore
    private Restaurant restaurant;
    private String name;
    private String type;
    private String description;
    private double price;
    private int num;
    private Date startDate;
    private Date endDate;
}
