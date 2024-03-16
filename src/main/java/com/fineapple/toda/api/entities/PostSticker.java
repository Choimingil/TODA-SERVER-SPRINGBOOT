package com.fineapple.toda.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="PostSticker")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostSticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long postStickerID;

    @Column(name="userID", nullable = false)
    private long userID;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="userID", insertable = false, updatable = false, referencedColumnName="ID")
    private User user;

    @Column(name="postID", nullable = false)
    private long postID;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name="postID", insertable = false, updatable = false, referencedColumnName="ID")
    private Post post;

    @Column(name="stickerID", nullable = false)
    private long stickerID;

    @ManyToOne(targetEntity = Sticker.class, fetch = FetchType.LAZY)
    @JoinColumn(name="stickerID", insertable = false, updatable = false, referencedColumnName="ID")
    private Sticker sticker;

    @Column(name="device", nullable = false)
    private int device;

    @Column(name="x", nullable = false)
    private double x;

    @Column(name="y", nullable = false)
    private double y;

    @Column(name="status", nullable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false)
    private LocalDateTime createAt;

    // PostStickerRotate와 PostStickerScale과의 매핑을 위함
    @Transient
    private int idx;
}
