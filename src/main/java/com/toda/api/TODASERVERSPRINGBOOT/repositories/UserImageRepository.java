package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UserImage SET status = 0 WHERE userID = :userID")
    void deleteImage(long userID);
}
