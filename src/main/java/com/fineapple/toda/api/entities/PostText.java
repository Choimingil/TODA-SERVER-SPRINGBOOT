package com.fineapple.toda.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="PostText")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long postTextID;

    @Column(name="postID", nullable = false)
    private long postID;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name="postID", insertable = false, updatable = false, referencedColumnName="ID")
    private Post post;

    @Column(name="text", nullable = false)
    private String text;

    @Column(name="aligned", nullable = false)
    private int status;
}
