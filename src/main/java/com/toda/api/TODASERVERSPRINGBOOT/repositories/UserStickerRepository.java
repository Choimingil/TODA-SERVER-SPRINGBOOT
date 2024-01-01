package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserSticker;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserStickerDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker,Long> {
    @Query("select s.stickerID from Sticker s inner join UserSticker us on s.stickerPackID = us.stickerPackID " +
            "where us.userID = :userID and status not like 0")
    Set<Long> getUserStickerSet(long userID);

    @Query("select us.userStickerID as userStickerID, sp.stickerPackID as stickerPackID, sp.image as miniticon from UserSticker us " +
            "inner join StickerPack sp on sp.stickerPackID = us.stickerPackID where us.userID = :userID and sp.status not like 0")
    List<UserStickerDetail> getUserStickers(long userID, Pageable pageable);
}
