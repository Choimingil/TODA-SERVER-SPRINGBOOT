package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.PostStickerScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@Repository
public interface PostStickerScaleRepository extends JpaRepository<PostStickerScale,Long> {
    List<PostStickerScale> findByUsedStickerIDIn(Set<Long> usedStickerIDSet);

    @Query("select pss from PostStickerScale pss inner join PostSticker ps on ps.postStickerID = pss.usedStickerID and ps.status not like 0 where ps.postStickerID in :postStickerIDSet")
    List<PostStickerScale> getPostStickerScale(Set<Long> postStickerIDSet, Pageable pageable);
}
