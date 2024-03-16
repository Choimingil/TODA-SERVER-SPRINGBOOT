package com.fineapple.toda.api.controllers;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.entities.Diary;
import com.fineapple.toda.api.entities.Post;
import com.fineapple.toda.api.models.responses.get.FcmResponse;
import com.fineapple.toda.api.services.FcmService;
import com.fineapple.toda.api.services.UserService;
import com.fineapple.toda.api.abstracts.AbstractController;
import com.fineapple.toda.api.abstracts.interfaces.BaseController;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import com.fineapple.toda.api.models.dtos.RegistrationId;
import com.fineapple.toda.api.models.responses.SuccessResponse;
import com.fineapple.toda.api.services.DiaryService;
import com.fineapple.toda.api.services.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class FcmController extends AbstractController implements BaseController {
    private final DiaryService diaryService;
    private final UserService userService;
    private final FcmService fcmService;
    private final PostService postService;

    public FcmController(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateUserAuth delegateUserAuth,
            UserService userService,
            FcmService fcmService,
            DiaryService diaryService,
            PostService postService
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.userService = userService;
        this.fcmService = fcmService;
        this.diaryService = diaryService;
        this.postService = postService;
    }

    // 다이어리 초대 알림 FCM 데이터 조회
    @GetMapping("/fcm/invite")
    public Map<String, ?> getInviteFcmResponse(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="receiveUserCode") String userCode,
            @RequestParam(name="diaryID") long diaryID
    ){
        UserDetail sendUser = getUserInfo(token);
        UserDetail receiveUser = userService.getUserInfoWithUserCode(userCode);

        List<RegistrationId> registrationIdList = fcmService.getSingleUserRegistrationIds(sendUser.getUser().getUserID(),receiveUser.getUser().getUserID());
        Diary diary = diaryService.getDiary(diaryID);
        FcmResponse res = fcmService.getInviteDiaryFcmResponse(registrationIdList,sendUser,diary);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }

    // 다이어리 승낙 알림 FCM 데이터 조회
    @GetMapping("/fcm/accept")
    public Map<String, ?> getAcceptFcmResponse(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="receiveUserCode") String userCode
    ){
        UserDetail sendUser = getUserInfo(token);
        UserDetail receiveUser = userService.getUserInfoWithUserCode(userCode);

        List<RegistrationId> registrationIdList = fcmService.getSingleUserRegistrationIds(sendUser.getUser().getUserID(),receiveUser.getUser().getUserID());
        FcmResponse res = fcmService.getAcceptDiaryFcmResponse(registrationIdList,sendUser);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }

    // 게시글 작성 알림 FCM 데이터 조회
    @GetMapping("/fcm/post")
    public Map<String, ?> getPostFcmResponse(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="diaryID") long diaryID,
            @RequestParam(name="postID") long postID
    ){
        UserDetail sendUser = getUserInfo(token);
        List<Long> receiveUserIDList = fcmService.getUserIDListWithDiaryID(sendUser.getUser().getUserID(), diaryID);

        List<RegistrationId> registrationIdList = fcmService.getMultiUserRegistrationIds(receiveUserIDList);
        FcmResponse res = fcmService.getPostFcmResponse(registrationIdList,sendUser,diaryID,postID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }

    // 좋아요 알림 FCM 데이터 조회
    @GetMapping("/fcm/like")
    public Map<String, ?> getLikeFcmResponse(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="postID") long postID
    ){
        UserDetail sendUser = getUserInfo(token);
        Post receivePost = postService.getPostByID(postID);

        List<RegistrationId> registrationIdList = fcmService.getSingleUserRegistrationIds(sendUser.getUser().getUserID(),receivePost.getUserID());
        FcmResponse res = fcmService.getLikeFcmResponse(registrationIdList,sendUser,receivePost.getUser(),receivePost.getDiaryID(), postID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }

    // 댓글 알림 FCM 데이터 조회
    @GetMapping("/fcm/comment")
    public Map<String, ?> getCommentFcmResponse(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="postID") long postID
    ){
        UserDetail sendUser = getUserInfo(token);
        Post receivePost = postService.getPostByID(postID);

        List<RegistrationId> registrationIdList = fcmService.getSingleUserRegistrationIds(sendUser.getUser().getUserID(),receivePost.getUserID());
        FcmResponse res = fcmService.getCommentFcmResponse(registrationIdList,sendUser,postID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }

    // 대댓글 알림 FCM 데이터 조회
    @GetMapping("/fcm/reply")
    public Map<String, ?> getReplyFcmResponse(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="postID") long postID,
            @RequestParam(name="commentID") long parentID
    ){
        UserDetail sendUser = getUserInfo(token);
        List<Long> receiveUserIDList = fcmService.getUserIDListWithParentID(sendUser.getUser().getUserID(), parentID);

        List<RegistrationId> registrationIdList = fcmService.getMultiUserRegistrationIds(receiveUserIDList);
        FcmResponse res = fcmService.getReplyFcmResponse(registrationIdList,sendUser,postID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }
}
