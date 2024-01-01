package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostSticker;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerRotate;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostStickerScale;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.StickerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class StickerController extends AbstractController implements BaseController {
    private final StickerService stickerService;

    //7-1. 유저 보유 스티커 조회 API
    @GetMapping("/user/stickers")
    public Map<String,?> getUserStickers(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        long userID = stickerService.getUserID(token);

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
    public Map<String, ?> addSticker(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid AddSticker addSticker,
            @PathVariable("postID") long postID,
            BindingResult bindingResult
    ){
        long userID = stickerService.getUserID(token);
        int userPostStatus = stickerService.getUserPostStatus(userID,postID);

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            // 추가한 스티커가 보유 중인 스티커가 맞는지 체크
            Set<Long> userStickerSet = stickerService.getUserStickerSet(userID);

            List<PostSticker> postStickerList = new ArrayList<>();
            List<PostStickerRotate> postStickerRotateList = new ArrayList<>();
            List<PostStickerScale> postStickerScaleList = new ArrayList<>();

            // PostSticker 추가해서 아이디값 얻기
            int idx = 0;
            for(AddStickerDetail addStickerDetail : addSticker.getStickerArr()){
                if(!userStickerSet.contains(addStickerDetail.getStickerID())) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_STICKER_EXCEPTION);
                stickerService.addPostSticker(postStickerList,userID,postID,addStickerDetail,idx++);
            }
            List<PostSticker> res = stickerService.savePostStickerList(postStickerList);

            // 얻은 아이디값 바탕으로 나머지값 추가
            for(PostSticker postSticker : res){
                stickerService.addPostStickerRotate(postStickerRotateList,postSticker.getPostStickerID(),addSticker.getStickerArr().get(postSticker.getIdx()));
                stickerService.addPostStickerScale(postStickerScaleList,postSticker.getPostStickerID(),addSticker.getStickerArr().get(postSticker.getIdx()));
            }
            stickerService.savePostStickerRotateAndScale(postStickerRotateList,postStickerScaleList);

            return new SuccessResponse.Builder(SuccessResponse.of.ADD_STICKER_SUCCESS).build().getResponse();
        }
    }

//    //23. 스티커 수정 API
//    @PatchMapping("/posts/{postID}/stickers")
//    public Map<String, ?> updateSticker(
//            @RequestHeader(TokenProvider.HEADER_NAME) String token,
//            @RequestBody @Valid UpdateSticker updateSticker,
//            @PathVariable("postID") long postID,
//            BindingResult bindingResult
//    ){
//        long userID = stickerService.getUserID(token);
//
//    }



    // $r->addRoute('GET', '/stickers/{stickerPackID:\d+}', ['StickerController', 'getStickerDetail']);                        //24-1. 스티커 상세 조회 API
    // $r->addRoute('GET', '/posts/{postID:\d+}/stickers', ['StickerController', 'getStickerView']);                           //25. 사용자가 사용한 스티커 리스트 조회 API



    // $r->addRoute('GET', '/store', ['StickerController', 'getStore']);                                                       //24-2. 스티커 상점 조회 API
}
