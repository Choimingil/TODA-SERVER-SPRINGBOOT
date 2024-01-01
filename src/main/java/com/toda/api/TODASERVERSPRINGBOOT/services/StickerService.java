package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.PostSticker;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerRotate;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerScale;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.AddSticker;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.AddStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component("stickerService")
@RequiredArgsConstructor
public class StickerService extends AbstractService implements BaseService {
    private final UserStickerRepository userStickerRepository;
    private final PostRepository postRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final PostStickerRepository postStickerRepository;
    private final PostStickerRotateRepository postStickerRotateRepository;
    private final PostStickerScaleRepository postStickerScaleRepository;

    private final TokenProvider tokenProvider;

    public List<UserStickerDetail> getUserStickers(long userID, int page){
        int start = (page-1)*10;
        Pageable pageable = PageRequest.of(start,10);
        return userStickerRepository.getUserStickers(userID,pageable);
    }

    public void addPostSticker(List<PostSticker> postStickerList, long userID, long postID, AddStickerDetail addStickerDetail, int idx){
        int status = getStatus(addStickerDetail.getLayerNum(), addStickerDetail.getInversion(), 10, () -> {});

        PostSticker postSticker = new PostSticker();
        postSticker.setUserID(userID);
        postSticker.setPostID(postID);
        postSticker.setStickerID(addStickerDetail.getStickerID());
        postSticker.setDevice(addStickerDetail.getDevice());
        postSticker.setX(addStickerDetail.getX());
        postSticker.setY(addStickerDetail.getY());
        postSticker.setStatus(status);
        postSticker.setIdx(idx);

        postStickerList.add(postSticker);
    }

    public void addPostStickerRotate(List<PostStickerRotate> postStickerRotateList, long postStickerID, AddStickerDetail addStickerDetail){
        PostStickerRotate postStickerRotate = new PostStickerRotate();
        postStickerRotate.setUsedStickerID(postStickerID);
        postStickerRotate.setA(addStickerDetail.getRotate().getA());
        postStickerRotate.setB(addStickerDetail.getRotate().getB());
        postStickerRotate.setC(addStickerDetail.getRotate().getC());
        postStickerRotate.setD(addStickerDetail.getRotate().getD());
        postStickerRotate.setTx(addStickerDetail.getRotate().getTx());
        postStickerRotate.setTy(addStickerDetail.getRotate().getTy());

        postStickerRotateList.add(postStickerRotate);
    }

    public void addPostStickerScale(List<PostStickerScale> postStickerScaleList, long postStickerID, AddStickerDetail addStickerDetail){
        PostStickerScale postStickerScale = new PostStickerScale();
        postStickerScale.setUsedStickerID(postStickerID);
        postStickerScale.setX(addStickerDetail.getScale().getX());
        postStickerScale.setY(addStickerDetail.getScale().getY());
        postStickerScale.setWidth(addStickerDetail.getScale().getWidth());
        postStickerScale.setHeight(addStickerDetail.getScale().getHeight());

        postStickerScaleList.add(postStickerScale);
    }




    @Transactional
    public List<PostSticker> savePostStickerList(List<PostSticker> postStickerList){return postStickerRepository.saveAll(postStickerList);}
    @Transactional
    public void savePostStickerRotateAndScale(List<PostStickerRotate> postStickerRotateList, List<PostStickerScale> postStickerScaleList){
        postStickerRotateRepository.saveAll(postStickerRotateList);
        postStickerScaleRepository.saveAll(postStickerScaleList);
    }
    public Set<Long> getUserStickerSet(long userID){return userStickerRepository.getUserStickerSet(userID);}
    public long getUserID(String token){return getUserID(token, tokenProvider);}
    public int getUserPostStatus(long userID, long postID){return getUserPostStatus(userID,postID,userDiaryRepository,postRepository);}
}
