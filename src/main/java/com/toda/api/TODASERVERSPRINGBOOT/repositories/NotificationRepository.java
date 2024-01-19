package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    void deleteByUserIDAndStatus(long userID, int status);
    Notification findByUserIDAndFcmAndStatusNot(long userID, String fcm, int status);
    List<Notification> findByUserIDAndIsAllowedAndStatusNot(long userID,String isAllowed,int status);
}
