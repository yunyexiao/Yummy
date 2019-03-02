package com.yyx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter @Setter
@Entity
@Table(name = "payment_account")
public class PaymentAccount implements Serializable {
    @Id
    private String id;
    private String pwd;
    @ManyToOne(optional = false)
    @JoinColumn(name = "email")
    @JsonIgnore
    private Customer customer;
    private double balance;
}
