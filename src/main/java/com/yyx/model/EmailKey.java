package com.yyx.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
@Entity
@Table(name = "email_key")
public class EmailKey implements Serializable {
    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "email")
    private Customer customer;

    private String key;

    @Override
    public boolean equals(Object o) {
        if (o == null)
            throw new NullPointerException();
        if (o == this)
            return true;
        if (o instanceof EmailKey) {
            EmailKey emailKey = (EmailKey) o;
            return emailKey.customer.equals(this.customer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 + customer.hashCode();
    }
}
