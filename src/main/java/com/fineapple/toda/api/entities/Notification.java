package com.fineapple.toda.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Notification")
@Getter
@Setter
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long notificationID;

    @Column(name="userID", nullable = false)
    private long userID;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="userID", insertable = false, updatable = false, referencedColumnName="ID")
    private User user;

    @Column(name="token", nullable = false)
    private String fcm;

    @Column(name="isAllowed", nullable = false)
    private String isAllowed;

    @Column(name="isRemindAllowed", nullable = false)
    private String isRemindAllowed;

    @Column(name="isEventAllowed", nullable = false)
    private String isEventAllowed;

    @Column(name="time", nullable = false, insertable = false)
    private String time;

    @Column(name="status", nullable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;

    public Notification(){}

    @Builder
    public Notification(
            long userID,
            String fcm,
            String isAllowed,
            String isRemindAllowed,
            String isEventAllowed,
            int status
    ){
        this.userID = userID;
        this.fcm = fcm;
        this.isAllowed = isAllowed;
        this.isRemindAllowed = isRemindAllowed;
        this.isEventAllowed = isEventAllowed;
        this.status = status;
    }
}
