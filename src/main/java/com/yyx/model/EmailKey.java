package com.yyx.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter @Setter
@Entity
@Table(name = "email_key")
public class EmailKey implements Serializable {
    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "email")
    private Customer customer;

    private String key;
}
