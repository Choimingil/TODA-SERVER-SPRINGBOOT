package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerRotate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@Repository
public interface PostStickerRotateRepository extends JpaRepository<PostStickerRotate,Long> {
    List<PostStickerRotate> findByUsedStickerIDIn(Set<Long> usedStickerIDSet);

    @Query("select psr from PostStickerRotate psr inner join PostSticker ps on ps.postStickerID = psr.usedStickerID and ps.status not like 0 where ps.postStickerID in :postStickerIDSet")
    List<PostStickerRotate> getPostStickerRotate(Set<Long> postStickerIDSet, Pageable pageable);
}
