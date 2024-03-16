package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StickerRepository extends JpaRepository<Sticker,Long> {
    List<Sticker> findByStickerPackID(long stickerPackID);
}
