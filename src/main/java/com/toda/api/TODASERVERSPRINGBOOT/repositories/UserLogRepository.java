package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserLogDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog,Long> {
    List<UserLog> findBySendIDAndReceiveIDAndTypeAndTypeID(long sendID, long receiveID, int type, long typeID);
    List<UserLog> findByReceiveIDAndTypeAndTypeIDAndStatusNot(long receiveID, int type, long typeID, int status);

    // @Query 내에는 ; 추가하면 안됨
    // @Query 내에는 엔티티 이름 그대로 사용하면 안됨

    @Query("select ul as userLog, " +
            "ui.url as selfie, " +
            "CASE WHEN ul.type > 2 THEN COALESCE((SELECT pi.url FROM PostImage pi WHERE pi.postID LIKE ul.sendID), '') ELSE '' END as image, " +
            "CASE WHEN ul.type = 1 AND ul.status = 100 THEN false ELSE true END as isReplied " +
            "from UserLog ul " +
            "inner join UserImage ui on ui.userID = ul.sendID and ui.status not like 0 " +
            "where ul.receiveID=:receiveID " +
            "order by ul.updateAt desc "
    )
    List<UserLogDetail> getUserLogs(long receiveID, Pageable pageable);
}
