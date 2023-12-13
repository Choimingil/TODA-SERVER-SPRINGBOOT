package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserFcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    void deleteByUserIDAndStatus(long userID, int status);
    boolean existsByNotificationIDAndIsRemindAllowed(long notificationID, String isRemindAllowed);
    boolean existsByUserIDAndFcmAndStatusNot(long userID, String fcm, int status);
    UserFcm findByUserIDAndFcmAndIsAllowedAndStatusNot(long userID, String fcm, String isAllowed, int status);
    List<UserFcm> findByUserIDAndIsAllowedAndStatusNot(long userID, String isAllowed, int status);
    Notification findByNotificationID(long notificationID);

    @Modifying
    @Transactional
    @Query("UPDATE Notification SET time = :time WHERE notificationID = :notificationID")
    void updateFcmTime(String time, long notificationID);
}
