package com.toda.api.TODASERVERSPRINGBOOT.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Announcement")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false)
    private long announcementID;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="text")
    private String text;

    @Column(name="image", nullable = false)
    private String image;

    @Column(name="status", nullable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateAt", nullable = false)
    private LocalDateTime updateAt;
}
