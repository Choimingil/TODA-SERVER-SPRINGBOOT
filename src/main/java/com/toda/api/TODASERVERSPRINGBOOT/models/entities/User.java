package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="User")
@Getter
@Setter
@ToString
//@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false)
    private long userID;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="code", nullable = false)
    private String userCode;

    @Column(name="status", nullable = false)
    private int appPassword;

    @Column(name="name", nullable = false)
    private String userName;

    @Column(name="point", nullable = false)
    private int point;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateAt", nullable = false)
    private LocalDateTime updateAt;

    User(){}

    @Builder
    User(
            long userID,
            String email,
            String password,
            String userCode,
            int appPassword,
            String userName,
            int point,
            LocalDateTime createAt,
            LocalDateTime updateAt
    ){
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.userCode = userCode;
        this.appPassword = appPassword;
        this.userName = userName;
        this.point = point;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }
}
