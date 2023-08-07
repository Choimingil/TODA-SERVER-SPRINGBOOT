package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserSticker;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserStickerDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker,Long> {
    boolean existsByUserIDAndStickerPackIDIn(long userID, List<Long> stickerPackIDList);
    List<UserSticker> findByUserID(long userID);

    @Query("select us.userStickerID as userStickerID, sp.stickerPackID as stickerPackID, sp.image as miniticon from UserSticker us " +
            "inner join StickerPack sp on sp.stickerPackID = us.stickerPackID where us.userID = :userID and sp.status not like 0"
    )
    List<UserStickerDetail> getUserStickers(long userID, Pageable pageable);

}
