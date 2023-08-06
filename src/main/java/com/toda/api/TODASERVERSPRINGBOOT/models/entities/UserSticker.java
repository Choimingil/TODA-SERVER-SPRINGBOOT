package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="UserSticker")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserSticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long userStickerID;

    @Column(name="userID", nullable = false)
    private long userID;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="userID", insertable = false, updatable = false, referencedColumnName="ID")
    private User user;

    @Column(name="stickerPackID", nullable = false)
    private long stickerPackID;

    @ManyToOne(targetEntity = StickerPack.class, fetch = FetchType.LAZY)
    @JoinColumn(name="stickerPackID", insertable = false, updatable = false, referencedColumnName="ID")
    private StickerPack stickerPack;

    @Column(name="status", nullable = false, insertable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
