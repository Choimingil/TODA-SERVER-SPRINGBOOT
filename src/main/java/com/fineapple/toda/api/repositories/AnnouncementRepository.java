package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.Announcement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {
    List<Announcement> findByStatusNotOrderByCreateAtDesc(int status, Pageable pageable);
    List<Announcement> findByStatusNotAndAnnouncementID(int status, long announcementID);
}
