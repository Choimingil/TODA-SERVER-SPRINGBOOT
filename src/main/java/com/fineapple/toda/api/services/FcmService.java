package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.entities.*;
import com.fineapple.toda.api.repositories.CommentRepository;
import com.fineapple.toda.api.repositories.NotificationRepository;
import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import com.fineapple.toda.api.models.dtos.RegistrationId;
import com.fineapple.toda.api.models.responses.get.FcmResponse;
import com.fineapple.toda.api.repositories.UserDiaryRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("fcmService")
public class FcmService extends AbstractService implements BaseService {
    private final NotificationRepository notificationRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final CommentRepository commentRepository;

    public FcmService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            NotificationRepository notificationRepository,
            UserDiaryRepository userDiaryRepository,
            CommentRepository commentRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.notificationRepository = notificationRepository;
        this.userDiaryRepository = userDiaryRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 다이어리 초대 알림 전송할 FcmResponse 객체 리턴
     * @param registrationIds
     * @param sendUser
     * @param diary
     * @return
     */
    public FcmResponse getInviteDiaryFcmResponse(List<RegistrationId> registrationIds, UserDetail sendUser, Diary diary){
        return FcmResponse.builder()
                .registration_ids(registrationIds)
                .title(getFcmTitle())
                .body(getFcmBody(
                        sendUser.getUser().getUserName(),
                        sendUser.getUser().getUserCode(),
                        diary.getDiaryName(),
                        1
                ))
                .type("addDiaryFriend")
                .data(diary.getDiaryID())
                .build();
    }

    /**
     * 다이어리 승낙 알림 전송할 FcmResponse 객체 리턴
     * @param registrationIds
     * @param sendUser
     * @return
     */
    public FcmResponse getAcceptDiaryFcmResponse(List<RegistrationId> registrationIds, UserDetail sendUser){
        return FcmResponse.builder()
                .registration_ids(registrationIds)
                .title(getFcmTitle())
                .body(getFcmBody(
                        sendUser.getUser().getUserName(),
                        sendUser.getUser().getUserCode(),
                        "",
                        2
                ))
                .type("acceptDiaryFriend")
                .build();
    }

    /**
     * 게시글 작성 알림 전송할 FcmResponse 객체 리턴
     * @param registrationIds
     * @param sendUser
     * @param diaryID
     * @param postID
     * @return
     */
    public FcmResponse getPostFcmResponse(List<RegistrationId> registrationIds, UserDetail sendUser, long diaryID, long postID){
        Map<String,Long> data = new HashMap<>();
        data.put("diaryID",diaryID);
        data.put("postID",postID);

        return FcmResponse.builder()
                .registration_ids(registrationIds)
                .title(getFcmTitle())
                .body(getFcmBody(
                        sendUser.getUser().getUserName(),
                        sendUser.getUser().getUserCode(),
                        "",
                        3
                ))
                .type("addPost")
                .data(data)
                .build();
    }

    /**
     * 좋아요 알림 전송할 FcmResponse 객체 리턴
     * @param registrationIds
     * @param sendUser
     * @param receiveUser
     * @param diaryID
     * @param postID
     * @return
     */
    public FcmResponse getLikeFcmResponse(List<RegistrationId> registrationIds, UserDetail sendUser, User receiveUser, long diaryID, long postID){
        Map<String,Long> data = new HashMap<>();
        data.put("diaryID",diaryID);
        data.put("postID",postID);

        return FcmResponse.builder()
                .registration_ids(registrationIds)
                .title(getFcmTitle())
                .body(getFcmBody(
                        sendUser.getUser().getUserName(),
                        sendUser.getUser().getUserCode(),
                        receiveUser.getUserName(),
                        4
                ))
                .type("postLike")
                .data(data)
                .build();
    }

    /**
     * 댓글 작성 알림 전송할 FcmResponse 객체 리턴
     * @param registrationIds
     * @param sendUser
     * @param postID
     * @return
     */
    public FcmResponse getCommentFcmResponse(List<RegistrationId> registrationIds, UserDetail sendUser, long postID){
        return FcmResponse.builder()
                .registration_ids(registrationIds)
                .title(getFcmTitle())
                .body(getFcmBody(
                        sendUser.getUser().getUserName(),
                        sendUser.getUser().getUserCode(),
                        "",
                        5
                ))
                .type("postComment")
                .data(postID)
                .build();
    }

    /**
     * 대댓글 작성 알림 전송할 FcmResponse 객체 리턴
     * @param registrationIds
     * @param sendUser
     * @param postID
     * @return
     */
    public FcmResponse getReplyFcmResponse(List<RegistrationId> registrationIds, UserDetail sendUser, long postID){
        return FcmResponse.builder()
                .registration_ids(registrationIds)
                .title(getFcmTitle())
                .body(getFcmBody(
                        sendUser.getUser().getUserName(),
                        sendUser.getUser().getUserCode(),
                        "",
                        6
                ))
                .type("postComment")
                .data(postID)
                .build();
    }


    /**
     * 한 명의 유저의 토큰 타입(IOS, AOS) 및 FCM 토큰 리스트 리턴
     * @param sendUserID
     * @param receiveUserID
     * @return
     */
    public List<RegistrationId> getSingleUserRegistrationIds(long sendUserID, long receiveUserID){
        if(sendUserID == receiveUserID) return new ArrayList<>();

        List<Notification> notificationList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(receiveUserID,"Y",0);
        return notificationList.stream().map(element -> {
            String type = element.getStatus()==100 ? "IOS" : "AOS";
            return RegistrationId.builder()
                    .type(type)
                    .token(element.getFcm())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 여러 명의 유저의 토큰 타입(IOS, AOS) 및 FCM 토큰 리스트 리턴
     * @param userIDList
     * @return
     */
    public List<RegistrationId> getMultiUserRegistrationIds(List<Long> userIDList){
        List<Notification> notificationList = notificationRepository.findByUserIDInAndIsAllowedAndStatusNot(userIDList,"Y",0);
        return notificationList.stream().map(element -> {
            String type = element.getStatus()==100 ? "IOS" : "AOS";
            return RegistrationId.builder()
                    .type(type)
                    .token(element.getFcm())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 현재 다이어리에 존재하는 알림 발송 유저 리스트
     * @param sendUserID
     * @param diaryID
     * @return
     */
    public List<Long> getUserIDListWithDiaryID(long sendUserID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByDiaryIDAndStatusNot(diaryID,999);
        return userDiaryList.stream().map(UserDiary::getUserID).filter(userID -> userID != sendUserID).collect(Collectors.toList());
    }

    /**
     * 댓글에 대댓글을 작성한 알림 발송 유저 리스트
     * @param sendUserID
     * @param parentID
     * @return
     */
    public List<Long> getUserIDListWithParentID(long sendUserID, long parentID){
        List<Comment> commentList = commentRepository.findByParentIDAndStatusNot(parentID,0);
        return commentList.stream().map(Comment::getUserID).filter(userID -> userID != sendUserID).collect(Collectors.toList());
    }
}
