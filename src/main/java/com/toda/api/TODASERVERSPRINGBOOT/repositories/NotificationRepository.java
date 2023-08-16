package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserFcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    void deleteByUserIDAndStatus(long userID, int status);
    boolean existsByUserIDAndStatusNot(long userID, int status);
    List<UserFcm> findByUserIDAndStatusNot(long userID, int status);
}
