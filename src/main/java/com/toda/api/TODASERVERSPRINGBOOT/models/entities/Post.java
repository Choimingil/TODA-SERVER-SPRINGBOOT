package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="Post")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long postID;

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

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="status", nullable = false, insertable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
