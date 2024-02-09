package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.DiaryNotice;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryMemberList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryNoticeRepository extends JpaRepository<DiaryNotice,Long> {
    List<DiaryNotice> findByUserIDAndDiaryIDAndStatusNot(long userID, long diaryID, int status);
    List<DiaryNotice> findByDiaryIDAndStatusNotOrderByCreateAtDesc(long diaryID, int status);
}
