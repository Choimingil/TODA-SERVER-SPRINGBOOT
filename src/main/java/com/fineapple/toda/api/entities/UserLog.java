package com.fineapple.toda.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="Log")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long userLogID;

    @Column(name="sendID", nullable = false)
    private long sendID;

    @Column(name="receiveID", nullable = false)
    private long receiveID;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="receiveID", insertable = false, updatable = false, referencedColumnName="ID")
    private User user;

    @Column(name="type", nullable = false)
    private long type;

    @Column(name="typeID", nullable = false)
    private long typeID;

    @Column(name="status", nullable = false, insertable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updateAt;
}
