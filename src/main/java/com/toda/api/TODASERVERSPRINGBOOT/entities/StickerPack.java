package com.toda.api.TODASERVERSPRINGBOOT.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="StickerPack")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StickerPack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long stickerPackID;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="point", nullable = false)
    private int point;

    @Column(name="miniticon", nullable = false)
    private String image;

    @Column(name="status", nullable = false, insertable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
