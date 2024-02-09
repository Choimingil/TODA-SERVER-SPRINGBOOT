package com.toda.api.TODASERVERSPRINGBOOT.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Comment")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long commentID;

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

    @Column(name="text", nullable = false)
    private String text;

    @Column(name="parent", nullable = false)
    private long parentID;

    @Column(name="status", nullable = false, insertable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false)
    private LocalDateTime createAt;
}
