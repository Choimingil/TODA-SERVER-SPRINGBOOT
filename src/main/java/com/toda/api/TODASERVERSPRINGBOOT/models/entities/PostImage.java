package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="PostImage")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long postImageID;

    @Column(name="postID", nullable = false)
    private long postID;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name="postID", insertable = false, updatable = false, referencedColumnName="ID")
    private Post post;

    @Column(name="URL", nullable = false)
    private String url;

    @Column(name="status", nullable = false, insertable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
