package com.example.demo.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
@Getter
@Setter
@ToString
public class Account implements Serializable {
    @Id
    @Column(length=20)
    private String accountId;
    @Column(nullable=false, length=30)
    private  String accountName;
    @Column(nullable=false, length=32)
    private  String password;
}