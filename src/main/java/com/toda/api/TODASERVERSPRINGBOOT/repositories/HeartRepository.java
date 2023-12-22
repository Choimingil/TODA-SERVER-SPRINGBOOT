package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRepository extends JpaRepository<Heart,Long> {
    List<Heart> findByUserIDAndPostIDOrderByCreateAtDesc(long userID, long postID);
}
