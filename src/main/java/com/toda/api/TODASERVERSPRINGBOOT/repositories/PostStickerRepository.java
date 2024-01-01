package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.PostSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostStickerRepository extends JpaRepository<PostSticker,Long> {

    Set<Long> findPostStickerIDByUserIDAndStatusNot(long userID, int status);
}
