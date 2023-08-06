package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerRepository extends JpaRepository<Sticker,Long> {
}
