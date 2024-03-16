package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage,Long> {
    List<PostImage> findByPostIDAndStatusNot(long postID, int status);
}
