package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.UserAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnnouncementRepository extends JpaRepository<UserAnnouncement,Long> {
    boolean existsByUserIDAndAnnouncementID(long userID, long announcementID);
    long countByUserID(long userID);
}
