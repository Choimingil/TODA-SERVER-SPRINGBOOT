package com.fineapple.toda.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="PostStickerRotate")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostStickerRotate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long postStickerRotateID;

    @Column(name="usedStickerID", nullable = false)
    private long usedStickerID;

    @ManyToOne(targetEntity = PostSticker.class, fetch = FetchType.LAZY)
    @JoinColumn(name="usedStickerID", insertable = false, updatable = false, referencedColumnName="ID")
    private PostSticker postSticker;

    @Column(name="a", nullable = false)
    private double a;

    @Column(name="b", nullable = false)
    private double b;

    @Column(name="c", nullable = false)
    private double c;

    @Column(name="d", nullable = false)
    private double d;

    @Column(name="tx", nullable = false)
    private double tx;

    @Column(name="ty", nullable = false)
    private double ty;
}
