package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    void deleteByUserID(long userID);
    UserImage findByUserIDAndStatusNot(long userID, int status);

    @Modifying
    @Query("UPDATE UserImage SET status = 0 WHERE userID = :userID")
    void deleteImage(long userID);
}
