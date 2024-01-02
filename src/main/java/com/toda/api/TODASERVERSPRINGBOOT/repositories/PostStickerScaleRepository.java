package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@Repository
public interface PostStickerScaleRepository extends JpaRepository<PostStickerScale,Long> {
//    @Query("select pss from PostStickerScale pss inner join PostSticker ps on ps.postStickerID = pss.usedStickerID and ps.status not like 0 where ps.postID = :postID")
//    List<PostStickerScale> getPostStickerScale(long postID, Pageable pageable);

    @Query("select pss from PostStickerScale pss inner join PostSticker ps on ps.postStickerID = pss.usedStickerID and ps.status not like 0 where ps.postStickerID in :postStickerIDSet")
    List<PostStickerScale> getPostStickerScale(Set<Long> postStickerIDSet, Pageable pageable);
}
