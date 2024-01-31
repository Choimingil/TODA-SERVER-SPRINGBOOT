package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.PostNotice;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UserCode;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.DiaryListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.DiaryMemberListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.DiaryNoticeResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.InviteRequestResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.DiaryService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DiaryController extends AbstractController implements BaseController {
    private final DiaryService diaryService;

    public DiaryController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, DiaryService diaryService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.diaryService = diaryService;
    }

    //11. 다이어리 추가 API
    @PostMapping("/diary")
    @SetMdcBody
    public Map<String, ?> createDiary(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid CreateDiary createDiary,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int status = diaryService.getDiaryStatus(createDiary.getStatus(), createDiary.getColor());
        long diaryID = diaryService.addDiary(createDiary.getTitle(), status);

        // 생성된 다이어리 유저 등록 & 공지 세팅
        diaryService.addUserDiary(userID,diaryID,createDiary.getTitle(),status);
        diaryService.addDiaryNotice(userID,diaryID,"");

        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_DIARY_SUCCESS)
                .build().getResponse();
    }

    //12. 다이어리 유저 추가 API
    @PostMapping("/diaries/{diaryID}/user")
    @SetMdcBody
    public Map<String, ?> setDiaryFriend(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID,
            @RequestParam(name="type", required = false) String type,
            @RequestBody @Valid UserCode userCode,
            BindingResult bindingResult
    ){
        /*
            SendUser : 자기 자신
            ReceiveUser : FCM 알림을 발송할 상대방
         */

        UserDetail sendUser = getUserInfo(token);
        UserDetail receiveUser = diaryService.getReceiveUserDetail(userCode.getUserCode());
        Diary diary = diaryService.getDiary(diaryID);

        // 자기 자신을 초대할 경우 예외 리턴
        long sendUserID = sendUser.getUser().getUserID();
        long receiveUserID = receiveUser.getUser().getUserID();
        if(sendUserID == receiveUserID) throw new BusinessLogicException(BusinessLogicException.of.WRONG_INVITE_EXCEPTION);

        int sendUserDiaryStatus = getUserDiaryStatus(sendUserID,diaryID);
        int receiveUserDiaryStatus = getUserDiaryStatus(receiveUserID,diaryID);

        // 현재 유저가 다이어리에 존재하지 않을 경우 불가능하므로 예외 리턴
        if(sendUserDiaryStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);

        // 현재 유저가 다이어리에 존재할 경우
        else if(sendUserDiaryStatus == 100){
            // 상대방 유저가 다이어리에 존재하지 않을 경우 다이어리 초대 진행
            if(receiveUserDiaryStatus == 404){
                // 다이어리 초대
                Map<Long,String> fcmDiaryInviteUserMap = diaryService.getFcmDiaryInviteUserMap(List.of(receiveUser));
                diaryService.inviteDiary(sendUser,receiveUser,diary);

                // FCM 발송
                diaryService.setFcmAndLog(fcmDiaryInviteUserMap, sendUser, diary, 1);
                return new SuccessResponse.Builder(SuccessResponse.of.INVITE_DIARY_SUCCESS).build().getResponse();
            }

            // 상대방 유저가 이미 초대를 받은 상태일 경우, 또는 이미 초대를 수락한 경우 예외 리턴
            else if(receiveUserDiaryStatus == 200) throw new BusinessLogicException(BusinessLogicException.of.ALREADY_INVITE_EXCEPTION);
            else throw new BusinessLogicException(BusinessLogicException.of.EXIST_USER_DIARY_EXCEPTION);
        }

        // 현재 유저가 다이어리 초대를 받은 경우 항상 다이어리 수락 진행
        else{
            // 다이어리 수락
            List<UserDiary> acceptableDiaryList = diaryService.getAcceptableDiaryList(sendUserID,receiveUserID,diaryID);
            Map<Long,String> acceptableDiaryMap = diaryService.getFcmDiaryAcceptUserMap(acceptableDiaryList);
            diaryService.acceptDiary(sendUserID, receiveUserID, diaryID, diary.getStatus(), acceptableDiaryList);

            // FCM 발송
            diaryService.setFcmAndLog(acceptableDiaryMap, sendUser, diary, 2);
            return new SuccessResponse.Builder(SuccessResponse.of.ACCEPT_DIARY_SUCCESS).build().getResponse();
        }
    }

    //12-1. 유저에게 온 다이어리 초대 요청 조회 API
    @GetMapping("/log/{diaryID}")
    public Map<String, ?> getInviteList(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        long userID = getUserID(token);
        List<InviteRequestResponse> responseList = diaryService.getInviteRequest(userID,diaryID);
        if(responseList.isEmpty()) return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS).add("result",new ArrayList<>()).build().getResponse();
        else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS).add("result",responseList.get(0)).build().getResponse();
    }

    //13. 다이어리 퇴장 및 초대 거절 API
    @DeleteMapping("/diary/{diaryID}")
    public Map<String, ?> deleteDiary(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,diaryID);

        // 현재 다이어리 속해 있는 경우 탈퇴 진행
        if(userDiaryStatus == 100){
            diaryService.deleteDiary(userID,diaryID);
            return new SuccessResponse.Builder(SuccessResponse.of.DELETE_DIARY_SUCCESS).build().getResponse();
        }
        // 초대 요청일 경우 초대 거절 진행
        else if(userDiaryStatus == 200){
            diaryService.rejectInvitation(userID,diaryID);
            return new SuccessResponse.Builder(SuccessResponse.of.REJECT_DIARY_SUCCESS).build().getResponse();
        }
        // 그 외의 경우 존재하지 않는 다이어리 예외 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    //14. 다이어리 수정 API
    @PatchMapping("/diary")
    @SetMdcBody
    public Map<String, ?> updateDiary(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdateDiary updateDiary,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,updateDiary.getDiary());

        // 현재 다이어리에 속해 있는 경우 수정 작업 진행
        if(userDiaryStatus == 100){
            diaryService.updateDiary(userID,updateDiary);
            return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_DIARY_SUCCESS).build().getResponse();
        }

        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    //15. 다이어리 조회 API
    @GetMapping("/diaries")
    public Map<String, ?> getDiaryList(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="page", required = true) int page,
            @RequestParam(name="status", required = true) int status,
            @RequestParam(name="keyword", required = false) String keyword
    ){
        long userID = getUserID(token);
        List<DiaryListResponse> res = keyword==null ?
                diaryService.getDiaryList(userID, status, page) :
                diaryService.getDiaryListWithKeyword(userID, status, page, keyword);

        // 다이어리가 존재하지 않을 경우 메시지 출력
        if(res.isEmpty()){
            return new SuccessResponse.Builder(
                    SuccessResponse.of.GET_SUCCESS.getCode(),
                    "등록된 다이어리가 없습니다."
            )
                    .add("result",res)
                    .build().getResponse();
        }
        // 다이어리 존재할 경우 다이어리 데이터 리턴
        else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",res)
                .build().getResponse();
    }

    //15-0. 다이어리 멤버 조회 API
    @GetMapping("/diaries/{diaryID}/users")
    public Map<String, ?> getDiaryMember(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID,
            @RequestParam(name="page", required = true) int page,
            @RequestParam(name="status", required = true) int status
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,diaryID);

        // 현재 다이어리에 속해 있는 경우 조회 진행
        if(userDiaryStatus == 100){
            List<DiaryMemberListResponse> res = diaryService.getDiaryMemberList(diaryID, status, page);

            // 다이어리가 존재하지 않을 경우 메시지 출력
            if(res.isEmpty()){
                return new SuccessResponse.Builder(
                        SuccessResponse.of.GET_SUCCESS.getCode(),
                        "등록된 친구가 없습니다."
                )
                        .add("result",res)
                        .build().getResponse();
            }
            // 다이어리 존재할 경우 다이어리 데이터 리턴
            else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",res)
                    .build().getResponse();
        }

        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    //15-1. 다이어리 공지 등록 API
    @PostMapping("/notice")
    @SetMdcBody
    public Map<String, ?> createNotice(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid PostNotice postNotice,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        diaryService.updateNotice(userID, postNotice.getDiary(), postNotice.getNotice());
        return new SuccessResponse.Builder(SuccessResponse.of.POST_DIARY_NOTICE_SUCCESS).build().getResponse();
    }

    //15-2. 다이어리 공지 삭제 API
    @DeleteMapping("/notice/{diaryID}")
    public Map<String, ?> createNotice(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        long userID = getUserID(token);
        diaryService.updateNotice(userID, diaryID, "");
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_DIARY_NOTICE_SUCCESS).build().getResponse();
    }

    //15-3. 다이어리 공지 수정 API
    @PatchMapping("/notice")
    @SetMdcBody
    public Map<String, ?> updateNotice(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid PostNotice postNotice,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        diaryService.updateNotice(userID, postNotice.getDiary(), postNotice.getNotice());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_DIARY_NOTICE_SUCCESS).build().getResponse();
    }

    //15-4. 다이어리 공지 조회 API
    @GetMapping("/notice/{diaryID}")
    public Map<String, ?> getNotice(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,diaryID);

        // 현재 다이어리에 속해 있는 경우 조회 진행
        if(userDiaryStatus == 100){
            DiaryNoticeResponse res = diaryService.getDiaryNotice(userID, diaryID);
            return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",res)
                    .build().getResponse();
        }

        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }
}
