package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.PostSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@Repository
public interface PostStickerRepository extends JpaRepository<PostSticker,Long> {

    List<PostSticker> findByUserIDAndStatusNot(long userID, int status);
    List<PostSticker> findByUserIDAndPostIDAndStatusNot(long userID, long postID, int status);
    List<PostSticker> findByPostIDAndStatusNot(long postID, int status, Pageable pageable);
}
