package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="User")
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long userID;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="code", nullable = false)
    private String userCode;

    @Column(name="status", nullable = false, insertable = false)
    private int appPassword;

    @Column(name="name", nullable = false)
    private String userName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;

    public User(){}

    @Builder
    public User(
            long userID,
            String email,
            String password,
            String userCode,
            int appPassword,
            String userName,
            LocalDateTime createAt
    ){
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.userCode = userCode;
        this.appPassword = appPassword;
        this.userName = userName;
        this.createAt = createAt;
    }
}
