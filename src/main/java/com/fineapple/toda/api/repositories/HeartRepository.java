package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRepository extends JpaRepository<Heart,Long> {
    List<Heart> findByUserIDAndPostIDOrderByCreateAtDesc(long userID, long postID);

    int countByPostIDAndStatusNot(long postID, int status);

    @Query("select ifnull((SELECT IF(h.userID = :userID, 1, 0) AS myLike FROM Heart h WHERE h.postID = :postID and h.status NOT LIKE 0), 0)")
    int getIsMyLike(long userID, long postID);
}
