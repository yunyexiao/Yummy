package com.yyx.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "`transaction`")
@NamedQueries({
        @NamedQuery(name = "Transaction.getPayment",
        query = "from Transaction where order.id = :oid and inAccount = 'YUMMY'")
})
public class Transaction implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "oid")
    private Order order;
    private String outAccount;
    private String inAccount;
    private double amount;
    private Timestamp time;

}
