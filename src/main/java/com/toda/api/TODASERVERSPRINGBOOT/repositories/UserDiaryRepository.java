package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryList;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryMemberList;
import org.springframework.data.domain.Pageable;
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
    List<UserDiary> findByDiaryIDAndStatusNot(long diaryID, int status);

    @Query("select u.userID as userID, u.userCode as userCode, u.email as email, u.userName as userName, " +
            "ui.url as selfie, ud.diaryID as diaryID, ud.diaryName as diaryName, TIMESTAMPDIFF(SECOND, ud.createAt, now()) as date from User u " +
            "inner join UserImage ui on u.userID = ui.userID and ui.status not like 0 " +
            "inner join UserDiary ud on ud.status/10 = u.userID and ud.diaryID= :diaryID " +
            "where ud.userID = :userID")
    List<DiaryRequestOfUser> getDiaryRequestOfUser(long diaryID, long userID);

    @Query("select ud as userDiary, " +
            "(select count(nud) from UserDiary nud " +
            "join nud.user u " +
            "where nud.diaryID = ud.diaryID and nud.status not like 999 " +
            "and u.appPassword not like 99999 " +
            "group by nud.diaryID) as userNum " +
            "from UserDiary ud where ud.userID = :userID and MOD(ud.status,100) = :status order by ud.createAt desc")
    List<DiaryList> getDiaryList(long userID, int status, Pageable pageable);

    @Query("select ud as userDiary, " +
            "(select count(nud) from UserDiary nud " +
            "join nud.user u " +
            "where nud.diaryID = ud.diaryID and nud.status not like 999 " +
            "and u.appPassword not like 99999 " +
            "group by nud.diaryID) as userNum " +
            "from UserDiary ud where ud.userID = :userID and MOD(ud.status,100) = :status and ud.diaryName like concat('%',:keyword,'%')" +
            "order by ud.createAt desc")
    List<DiaryList> getDiaryListWithKeyword(long userID, int status, Pageable pageable, String keyword);

    @Query("select ud as userDiary, " +
            "(select count(nud) from UserDiary nud " +
            "join nud.user u " +
            "where nud.diaryID = ud.diaryID and nud.status not like 999 " +
            "and u.appPassword not like 99999 " +
            "group by nud.diaryID) as userNum, " +
            "ui.url as selfie " +
            "from UserDiary ud " +
            "inner join UserImage ui on ui.userID = ud.userID and ui.status not like 0 " +
            "where ud.diaryID = :diaryID and MOD(ud.status,100) = :status order by ui.createAt desc")
    List<DiaryMemberList> getDiaryMemberList(long diaryID, int status, Pageable pageable);
}
