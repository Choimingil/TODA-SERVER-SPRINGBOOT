package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.StickerPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerPackRepository extends JpaRepository<StickerPack,Long> {
    StickerPack findByStickerPackIDAndStatusNot(long stickerPackID, int status);
}
