package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.DiaryNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryNoticeRepository extends JpaRepository<DiaryNotice,Long> {
    List<DiaryNotice> findByUserIDAndDiaryIDAndStatusNot(long userID, long diaryID, int status);
    List<DiaryNotice> findByDiaryIDAndStatusNotOrderByCreateAtDesc(long diaryID, int status);
}
