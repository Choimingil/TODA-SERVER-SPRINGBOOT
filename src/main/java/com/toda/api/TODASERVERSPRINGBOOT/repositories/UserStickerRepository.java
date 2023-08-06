package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker,Long> {
    boolean existsByUserIDAndStickerPackIDIn(long userID, List<Long> stickerPackIDList);
}
