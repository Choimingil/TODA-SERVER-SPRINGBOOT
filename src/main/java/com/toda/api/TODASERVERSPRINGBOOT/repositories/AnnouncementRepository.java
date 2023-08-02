package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Announcement;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.AnnouncementDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.AnnouncementList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {
    List<AnnouncementList> findByStatusNotOrderByCreateAtDesc(int status, Pageable pageable);
    List<AnnouncementDetail> findByStatusNotAndAnnouncementID(int status, long announcementID);
}
