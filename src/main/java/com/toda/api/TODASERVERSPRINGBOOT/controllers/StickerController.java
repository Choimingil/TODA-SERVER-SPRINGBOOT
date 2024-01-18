package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostSticker;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerRotate;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerScale;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.StickerService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class StickerController extends AbstractController implements BaseController {
    private final StickerService stickerService;

    public StickerController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, StickerService stickerService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.stickerService = stickerService;
    }

    //7-1. 유저 보유 스티커 조회 API
    @GetMapping("/user/stickers")
    public Map<String,?> getUserStickers(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        long userID = getUserID(token);

        List<UserStickerDetail> userStickers = stickerService.getUserStickers(userID,page);
        List<Map<String,?>> result = userStickers.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ID", element.getUserStickerID());
            map.put("stickerPackID", element.getStickerPackID());
            map.put("miniticon", element.getMiniticon());
            return map;
        }).collect(Collectors.toList());

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",result)
                .build().getResponse();
    }

    //22. 스티커 사용 API
    @PostMapping("/posts/{postID}/stickers")
    @SetMdcBody
    public Map<String, ?> addSticker(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid AddSticker addSticker,
            @PathVariable("postID") long postID,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,postID);

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            // 추가한 스티커가 보유 중인 스티커가 맞는지 체크
            Set<Long> userStickerSet = stickerService.getUserStickerSet(userID);

            Set<PostSticker> postStickerSet = new HashSet<>();
            Set<PostStickerRotate> postStickerRotateSet = new HashSet<>();
            Set<PostStickerScale> postStickerScaleSet = new HashSet<>();

            // PostSticker 추가해서 아이디값 얻기
            int idx = 0;
            for(AddStickerDetail addStickerDetail : addSticker.getStickerArr()){
                if(!userStickerSet.contains(addStickerDetail.getStickerID())) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_STICKER_EXCEPTION);
                stickerService.addPostSticker(postStickerSet,userID,postID,addStickerDetail,idx++);
            }
            List<PostSticker> res = stickerService.savePostStickerSet(postStickerSet);

            // 얻은 아이디값 바탕으로 나머지값 추가
            for(PostSticker postSticker : res){
                AddStickerDetail curr = addSticker.getStickerArr().get(postSticker.getIdx());
                stickerService.setPostStickerRotate(
                        postStickerRotateSet,
                        postSticker.getPostStickerID(),
                        curr.getRotate().getA(),
                        curr.getRotate().getB(),
                        curr.getRotate().getC(),
                        curr.getRotate().getD(),
                        curr.getRotate().getTx(),
                        curr.getRotate().getTy()
                );
                stickerService.setPostStickerScale(
                        postStickerScaleSet,
                        postSticker.getPostStickerID(),
                        curr.getScale().getX(),
                        curr.getScale().getY(),
                        curr.getScale().getWidth(),
                        curr.getScale().getHeight()
                );
            }
            stickerService.savePostStickerRotateAndScaleSet(postStickerRotateSet,postStickerScaleSet);

            return new SuccessResponse.Builder(SuccessResponse.of.ADD_STICKER_SUCCESS).build().getResponse();
        }
    }

    //23. 스티커 수정 API
    @PatchMapping("/posts/{postID}/stickers")
    @SetMdcBody
    public Map<String, ?> updateSticker(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdateSticker updateSticker,
            @PathVariable("postID") long postID,
            BindingResult bindingResult
    ){
        // postStickerID로 PostSticker 값 가져올 수 있는 Map 설정
        long userID = getUserID(token);
        List<PostSticker> userPostStickerList = stickerService.getUserPostStickerList(userID,postID);
        Map<Long, PostSticker> userPostStickerMap = userPostStickerList.stream()
                .collect(Collectors.toMap(PostSticker::getPostStickerID, Function.identity()));

        Set<PostSticker> postStickerSet = new HashSet<>();
        Set<PostStickerRotate> postStickerRotateSet = new HashSet<>();
        Set<PostStickerScale> postStickerScaleSet = new HashSet<>();

        for(UpdateStickerDetail updateStickerDetail : updateSticker.getUsedStickerArr()){
            // 입력받은 스티커 아이디가 실제 유저가 추가한 스티커인지 확인
            if(!userPostStickerMap.containsKey(updateStickerDetail.getUsedStickerID()))
                throw new BusinessLogicException(BusinessLogicException.of.NO_USER_STICKER_EXCEPTION);

            // 수정하려는 스티커 아이디값의 PostSticker를 Map에서 찾아서 수정 데이터로 값 대체
            PostSticker curr = userPostStickerMap.get(updateStickerDetail.getUsedStickerID());
            stickerService.updatePostSticker(postStickerSet,curr,updateStickerDetail);
            stickerService.setPostStickerRotate(
                    postStickerRotateSet,
                    updateStickerDetail.getUsedStickerID(),
                    updateStickerDetail.getRotate().getA(),
                    updateStickerDetail.getRotate().getB(),
                    updateStickerDetail.getRotate().getC(),
                    updateStickerDetail.getRotate().getD(),
                    updateStickerDetail.getRotate().getTx(),
                    updateStickerDetail.getRotate().getTy()
            );
            stickerService.setPostStickerScale(
                    postStickerScaleSet,
                    updateStickerDetail.getUsedStickerID(),
                    updateStickerDetail.getScale().getX(),
                    updateStickerDetail.getScale().getY(),
                    updateStickerDetail.getScale().getWidth(),
                    updateStickerDetail.getScale().getHeight()
            );
        }

        // 유저가 추가한 모든 스티커 중 수정되지 않은 스티커들은 모두 삭제 처리
        for(PostSticker postSticker : userPostStickerList){
            if(!postStickerSet.contains(postSticker)) stickerService.deletePostSticker(postStickerSet,postSticker);
        }

        // DB 수정 내용 반영
        stickerService.savePostStickerSet(postStickerSet);
        stickerService.savePostStickerRotateAndScaleSet(postStickerRotateSet,postStickerScaleSet);
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_STICKER_SUCCESS).build().getResponse();
    }

    //24. 스티커 상세 조회 API
    @GetMapping("/stickers/{stickerPackID}")
    public Map<String, ?> getStickerDetail(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("stickerPackID") long stickerPackID
    ){
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",stickerService.getStickerDetail(stickerPackID))
                    .build().getResponse();
    }

    //25. 게시글에 사용된 스티커 리스트 조회 API
    @GetMapping("/posts/{postID}/stickers")
    public Map<String, ?> getPostStickerList(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("postID") long postID,
            @RequestParam(name="page", required = true) int page
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,postID);

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",stickerService.getPostStickerList(userID,postID,page))
                    .build().getResponse();
        }
    }



    // $r->addRoute('GET', '/store', ['StickerController', 'getStore']);                                                       //24-2. 스티커 상점 조회 API
}
