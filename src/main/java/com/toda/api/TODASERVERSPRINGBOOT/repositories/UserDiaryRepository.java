package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryRequestOfUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDiaryRepository extends JpaRepository<UserDiary,Long> {
    List<UserDiary> findByUserIDAndDiaryIDAndStatus(long userID, long diaryID, int status);
    List<UserDiary> findByUserIDAndDiaryIDAndStatusNot(long userID, long diaryID, int status);
    List<UserDiary> findByUserIDAndDiaryID(long userID, long diaryID);

    @Query("select u.userID as userID, u.userCode as userCode, u.email as email, u.userName as userName, " +
            "ui.url as selfie, ud.diaryID as diaryID, ud.diaryName as diaryName, TIMESTAMPDIFF(SECOND, ud.createAt, now()) as date from User u " +
            "inner join UserImage ui on u.userID = ui.userID and ui.status not like 0 " +
            "inner join UserDiary ud on ud.status/10 = u.userID and ud.diaryID= :diaryID " +
            "where ud.userID = :userID")
    List<DiaryRequestOfUser> getDiaryRequestOfUser(long diaryID, long userID);

//    @Query("select u.userID as userID, u.userCode as userCode, u.email as email, u.userName as userName, " +
//            "ui.url as selfie, ud.diaryID as diaryID, ud.diaryName as diaryName, TIMESTAMPDIFF(SECOND, ud.createAt, now()) as date " +
//            "from User u " +
//            "inner join UserImage ui on u.userID = ui.userID and ui.status not like 0 " +
//            "inner join UserDiary ud on ud.status/10 = u.userID and ud.diaryID= :diaryID " +
//            "where ud.userID = :userID")
//    List<DiaryRequestOfUser> getDiaryRequestOfUser(long diaryID, long userID);



//    @Modifying
//    @Transactional
//    @Query("UPDATE UserDiary SET status=(SELECT status from Diary WHERE diaryID = :diaryID) WHERE diaryID=:diaryID and userID=:userID")
//    void updateUserDiaryStatus(long diaryID, long userID);
}
