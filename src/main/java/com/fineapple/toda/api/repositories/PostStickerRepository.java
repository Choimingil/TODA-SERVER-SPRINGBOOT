package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.PostSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface PostStickerRepository extends JpaRepository<PostSticker,Long> {

    List<PostSticker> findByUserIDAndStatusNot(long userID, int status);
    List<PostSticker> findByUserIDAndPostIDAndStatusNot(long userID, long postID, int status);
    List<PostSticker> findByPostIDAndStatusNot(long postID, int status, Pageable pageable);
}
