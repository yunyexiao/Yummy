package com.yyx.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "customer")
@NamedQueries({
        @NamedQuery(name = "Customer.count",
                query = "select count(email) from Customer where valid = 1")
})
public class Customer implements Serializable {
    @Id
    private String email;

    private String pwd;
    private String name;
    private String phone;
    /**
     * range: 0-8
     */
    private int level;
    private int valid;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmailKey emailKey;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("address asc")
    private List<AddressInfo> addressInfos;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<PaymentAccount> accounts;

    public boolean isValid() {
        return valid == 1;
    }

}
