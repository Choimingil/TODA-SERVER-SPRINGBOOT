package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="UserDiary")
@Getter
@Setter
@ToString
public class UserDiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long userDiaryID;

    @Column(name="userID", nullable = false)
    private long userID;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="userID", insertable = false, updatable = false, referencedColumnName="ID")
    private User user;

    @Column(name="diaryID", nullable = false)
    private long diaryID;

    @ManyToOne(targetEntity = Diary.class, fetch = FetchType.LAZY)
    @JoinColumn(name="diaryID", insertable = false, updatable = false, referencedColumnName="ID")
    private Diary diary;

    @Column(name="diaryName", nullable = false)
    private String diaryName;

    @Column(name="status", nullable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
