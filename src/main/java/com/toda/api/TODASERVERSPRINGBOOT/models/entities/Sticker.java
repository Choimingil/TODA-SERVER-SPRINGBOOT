package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Sticker")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Sticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long stickerID;

    @Column(name="stickerPackID", nullable = false)
    private long stickerPackID;

    @ManyToOne(targetEntity = StickerPack.class, fetch = FetchType.LAZY)
    @JoinColumn(name="stickerPackID", insertable = false, updatable = false, referencedColumnName="ID")
    private StickerPack stickerPack;

    @Column(name="URL", nullable = false)
    private String image;

    @Column(name="count", nullable = false)
    private long count;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
