package com.fineapple.toda.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="PostStickerScale")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostStickerScale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long postStickerScaleID;

    @Column(name="usedStickerID", nullable = false)
    private long usedStickerID;

    @ManyToOne(targetEntity = PostSticker.class, fetch = FetchType.LAZY)
    @JoinColumn(name="usedStickerID", insertable = false, updatable = false, referencedColumnName="ID")
    private PostSticker postSticker;

    @Column(name="x", nullable = false)
    private double x;

    @Column(name="y", nullable = false)
    private double y;

    @Column(name="width", nullable = false)
    private double width;

    @Column(name="height", nullable = false)
    private double height;
}
