package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
import com.fineapple.toda.api.entities.*;
import com.fineapple.toda.api.entities.mappings.UserStickerDetail;
import com.fineapple.toda.api.models.bodies.AddStickerDetail;
import com.fineapple.toda.api.models.bodies.StickerRotate;
import com.fineapple.toda.api.models.bodies.StickerScale;
import com.fineapple.toda.api.models.bodies.UpdateStickerDetail;
import com.fineapple.toda.api.models.responses.get.PostStickerListResponse;
import com.fineapple.toda.api.models.responses.get.StickerDetailResponse;
import com.fineapple.toda.api.models.responses.get.StickerPackDetailResponse;
import com.fineapple.toda.api.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("stickerService")
public class StickerService extends AbstractService implements BaseService {
    private final UserStickerRepository userStickerRepository;
    private final PostStickerRepository postStickerRepository;
    private final PostStickerRotateRepository postStickerRotateRepository;
    private final PostStickerScaleRepository postStickerScaleRepository;
    private final StickerRepository stickerRepository;
    private final StickerPackRepository stickerPackRepository;

    public StickerService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            UserStickerRepository userStickerRepository,
            PostStickerRepository postStickerRepository,
            PostStickerRotateRepository postStickerRotateRepository,
            PostStickerScaleRepository postStickerScaleRepository,
            StickerRepository stickerRepository,
            StickerPackRepository stickerPackRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.userStickerRepository = userStickerRepository;
        this.postStickerRepository = postStickerRepository;
        this.postStickerRotateRepository = postStickerRotateRepository;
        this.postStickerScaleRepository = postStickerScaleRepository;
        this.stickerRepository = stickerRepository;
        this.stickerPackRepository = stickerPackRepository;
    }

    public List<UserStickerDetail> getUserStickers(long userID, int page){
        int start = (page-1)*10;
        Pageable pageable = PageRequest.of(start,10);
        return userStickerRepository.getUserStickers(userID,pageable);
    }

    public void addPostSticker(Set<PostSticker> postStickerSet, long userID, long postID, AddStickerDetail addStickerDetail, int idx){
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

        postStickerSet.add(postSticker);
    }

    private PostStickerRotate addPostStickerRotate(
            long postStickerID,
            double a,
            double b,
            double c,
            double d,
            double tx,
            double ty
    ){
        PostStickerRotate postStickerRotate = new PostStickerRotate();
        postStickerRotate.setUsedStickerID(postStickerID);
        postStickerRotate.setA(a);
        postStickerRotate.setB(b);
        postStickerRotate.setC(c);
        postStickerRotate.setD(d);
        postStickerRotate.setTx(tx);
        postStickerRotate.setTy(ty);
        return postStickerRotate;
    }

    private PostStickerScale addPostStickerScale(
            long postStickerID,
            double x,
            double y,
            double width,
            double height
    ){
        PostStickerScale postStickerScale = new PostStickerScale();
        postStickerScale.setUsedStickerID(postStickerID);
        postStickerScale.setX(x);
        postStickerScale.setY(y);
        postStickerScale.setWidth(width);
        postStickerScale.setHeight(height);
        return postStickerScale;
    }

    public void setPostStickerRotate(
            Set<PostStickerRotate> postStickerRotateSet,
            long postStickerID,
            double a,
            double b,
            double c,
            double d,
            double tx,
            double ty
    ){
        PostStickerRotate postStickerRotate = addPostStickerRotate(postStickerID,a,b,c,d,tx,ty);
        postStickerRotateSet.add(postStickerRotate);
    }

    public void setPostStickerScale(
            Set<PostStickerScale> postStickerScaleSet,
            long postStickerID,
            double x,
            double y,
            double width,
            double height
    ){
        PostStickerScale postStickerScale = addPostStickerScale(postStickerID,x,y,width,height);
        postStickerScaleSet.add(postStickerScale);
    }

    public void updatePostSticker(Set<PostSticker> postStickerSet, PostSticker postSticker, UpdateStickerDetail updateStickerDetail){
        int status = getStatus(updateStickerDetail.getLayerNum(), updateStickerDetail.getInversion(), 10, () -> {});

        postSticker.setDevice(updateStickerDetail.getDevice());
        postSticker.setX(updateStickerDetail.getX());
        postSticker.setY(updateStickerDetail.getY());
        postSticker.setStatus(status);

        postStickerSet.add(postSticker);
    }

    public void updatePostStickerRotate(
            Map<Long, PostStickerRotate> input,
            Set<PostStickerRotate> output,
            long postStickerID,
            double a,
            double b,
            double c,
            double d,
            double tx,
            double ty
    ){
        PostStickerRotate postStickerRotate = input.get(postStickerID);
        if(postStickerRotate == null) postStickerRotate = addPostStickerRotate(postStickerID,a,b,c,d,tx,ty);
        else{
            postStickerRotate.setA(a);
            postStickerRotate.setB(b);
            postStickerRotate.setC(c);
            postStickerRotate.setD(d);
            postStickerRotate.setTx(tx);
            postStickerRotate.setTy(ty);
        }

        output.add(postStickerRotate);
    }

    public void updatePostStickerScale(
            Map<Long, PostStickerScale> input,
            Set<PostStickerScale> output,
            long postStickerID,
            double x,
            double y,
            double width,
            double height
    ){
        PostStickerScale postStickerScale = input.get(postStickerID);
        if(postStickerScale == null) postStickerScale = addPostStickerScale(postStickerID,x,y,width,height);
        else{
            postStickerScale.setX(x);
            postStickerScale.setY(y);
            postStickerScale.setWidth(width);
            postStickerScale.setHeight(height);
        }

        output.add(postStickerScale);
    }

    public void deletePostSticker(Set<PostSticker> postStickerSet, PostSticker postSticker){
        postSticker.setStatus(0);
        postStickerSet.add(postSticker);
    }

    public StickerPackDetailResponse getStickerDetail(long stickerPackID){
        List<Sticker> stickerList = stickerRepository.findByStickerPackID(stickerPackID);
        List<StickerDetailResponse> stickerDetailResponseList = stickerList.stream().map(sticker ->
                StickerDetailResponse.builder().stickerID(sticker.getStickerID()).image(sticker.getImage()).build()).toList();

        StickerPack stickerPack = stickerPackRepository.findByStickerPackIDAndStatusNot(stickerPackID,0);
        return StickerPackDetailResponse.builder()
                .stickerPackID(stickerPackID)
                .name(stickerPack.getName())
                .point(stickerPack.getPoint())
                .stickerArr(stickerDetailResponseList)
                .build();
    }

    public List<PostStickerListResponse> getPostStickerList(long userID, long postID, int page){
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);

        // PostStickerList 가져오기
        List<PostSticker> postStickerList = postStickerRepository.findByPostIDAndStatusNot(postID,0,pageable);
        Set<Long> postStickerIDSet = postStickerList.stream().map(PostSticker::getPostStickerID).collect(Collectors.toSet());

        // PostStickerID에 맞추어 매핑을 위해 Map<Long,PostStickerRotate || PostStickerScale> 생성
        Map<Long, PostStickerRotate> rotateMap =
                postStickerRotateRepository.getPostStickerRotate(postStickerIDSet,pageable).stream()
                        .collect(Collectors.toMap(PostStickerRotate::getUsedStickerID, psr -> psr));
        Map<Long, PostStickerScale> scaleMap =
                postStickerScaleRepository.getPostStickerScale(postStickerIDSet,pageable).stream()
                        .collect(Collectors.toMap(PostStickerScale::getUsedStickerID, psr -> psr));

        return postStickerList.stream()
                .filter(postSticker -> rotateMap.containsKey(postSticker.getPostStickerID()) && scaleMap.containsKey(postSticker.getPostStickerID()))
                .map(postSticker -> {
                    int inversion = postSticker.getStatus() % 10;
                    int layerNum = postSticker.getStatus() / 10;

                    PostStickerRotate psr = rotateMap.get(postSticker.getPostStickerID());
                    PostStickerScale pss = scaleMap.get(postSticker.getPostStickerID());

                    return PostStickerListResponse.builder()
                            .postID(postID)
                            .usedStickerID(postSticker.getPostStickerID())
                            .userID(userID)
                            .stickerID(postSticker.getStickerID())
                            .image(postSticker.getSticker().getImage())
                            .device(postSticker.getDevice())
                            .x(postSticker.getX())
                            .y(postSticker.getY())
                            .rotate(StickerRotate.builder()
                                    .a(psr.getA())
                                    .b(psr.getB())
                                    .c(psr.getC())
                                    .d(psr.getD())
                                    .tx(psr.getTx())
                                    .ty(psr.getTy())
                                    .build())
                            .scale(StickerScale.builder()
                                    .x(pss.getX())
                                    .y(pss.getY())
                                    .width(pss.getWidth())
                                    .height(pss.getHeight())
                                    .build())
                            .inversion(inversion)
                            .layerNum(layerNum)
                            .isMySticker(postSticker.getUserID() == userID)
                            .build();
                })
                .collect(Collectors.toList());

    }

    public Map<Long, PostStickerRotate> getPostStickerRotateMap(Set<Long> usedStickerID){
        List<PostStickerRotate> postStickerRotateList = postStickerRotateRepository.findByUsedStickerIDIn(usedStickerID);
        return postStickerRotateList.stream()
                .collect(Collectors.toMap(PostStickerRotate::getUsedStickerID, Function.identity()));
    }

    public Map<Long, PostStickerScale> getPostStickerScaleMap(Set<Long> usedStickerID){
        List<PostStickerScale> postStickerScaleList = postStickerScaleRepository.findByUsedStickerIDIn(usedStickerID);
        return postStickerScaleList.stream()
                .collect(Collectors.toMap(PostStickerScale::getUsedStickerID, Function.identity()));
    }


    @Transactional
    public List<PostSticker> savePostStickerSet(Set<PostSticker> postStickerSet){return postStickerRepository.saveAll(postStickerSet);}
    @Transactional
    public void savePostStickerRotateAndScaleSet(Set<PostStickerRotate> postStickerRotateSet, Set<PostStickerScale> postStickerScaleSet){
        postStickerRotateRepository.saveAll(postStickerRotateSet);
        postStickerScaleRepository.saveAll(postStickerScaleSet);
    }



    public List<PostSticker> getPostStickerList(long userID){return postStickerRepository.findByUserIDAndStatusNot(userID,0);}
    public List<PostSticker> getUserPostStickerList(long userID, long postID){return postStickerRepository.findByUserIDAndPostIDAndStatusNot(userID,postID,0);}
    public Set<Long> getUserStickerSet(long userID){return userStickerRepository.getUserStickerSet(userID);}
}
