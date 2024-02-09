package com.toda.api.TODASERVERSPRINGBOOT.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="UserAnnouncement")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID", nullable = false, insertable = false, updatable = false)
    private long userAnnouncementID;

    @Column(name="userID", nullable = false)
    private long userID;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="userID", insertable = false, updatable = false, referencedColumnName="ID")
    private User user;

    @Column(name="announcementID", nullable = false)
    private long announcementID;

    @ManyToOne(targetEntity = Announcement.class, fetch = FetchType.LAZY)
    @JoinColumn(name="announcementID", insertable = false, updatable = false, referencedColumnName="ID")
    private Announcement announcement;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createAt", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createAt;
}
