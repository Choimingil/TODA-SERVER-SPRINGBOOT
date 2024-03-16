package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Long> {
    Diary findByDiaryID(long diaryID);
}
