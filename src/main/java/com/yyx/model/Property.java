package com.yyx.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter @Setter
@Entity
@Table(name = "properties")
public class Property implements Serializable {
    @Id
    private String name;
    private String value;
}
