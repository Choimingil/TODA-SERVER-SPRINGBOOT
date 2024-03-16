package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.PostText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTextRepository extends JpaRepository<PostText,Long> {
    List<PostText> findByPostID(long postID);
}
