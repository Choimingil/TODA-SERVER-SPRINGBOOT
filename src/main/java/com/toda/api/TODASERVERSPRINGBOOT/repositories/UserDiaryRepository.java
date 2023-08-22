package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserDiaryRepository extends JpaRepository<UserDiary,Long> {
    UserDiary findByUserIDAndDiaryID(long userID, long diaryID);

    @Query("SELECT EXISTS(SELECT 1 FROM UserDiary WHERE userID = :userID and diaryID = :diaryID and status%10 like 0)")
    boolean isSendRequest(long userID, long diaryID);



//    @Modifying
//    @Transactional
//    @Query("UPDATE UserDiary SET status=(SELECT status from Diary WHERE diaryID = :diaryID) WHERE diaryID=:diaryID and userID=:userID")
//    void updateUserDiaryStatus(long diaryID, long userID);
}
