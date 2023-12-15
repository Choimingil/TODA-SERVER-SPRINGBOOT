package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryRequestOfUser;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateNotice;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UserCode;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryMemberListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryNoticeResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DiaryController extends AbstractController implements BaseController {
    private final DiaryService diaryService;

    //11. 다이어리 추가 API
    @PostMapping("/diary")
    public Map<String, ?> createDiary(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid CreateDiary createDiary,
            BindingResult bindingResult
    ){
        long userID = diaryService.getUserID(token);
        int status = diaryService.getDiaryStatus(createDiary.getStatus(), createDiary.getColor());
        long diaryID = diaryService.addDiary(createDiary.getTitle(), status);
        diaryService.setDiaryInfo(userID,diaryID, createDiary.getTitle(),status);
        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_DIARY_SUCCESS)
                .build().getResponse();
    }

    //12. 다이어리 유저 추가 API
    @PostMapping("/diaries/{diaryID}/user")
    public Map<String, ?> setDiaryFriend(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID,
            @RequestParam(name="type", required = false) String type,
            @RequestBody @Valid UserCode userCode,
            BindingResult bindingResult
    ){
        /*
            SendUser : 자기 자신
            ReceiveUser : FCM 알림을 발송할 상대방
         */

        UserData sendUserData = diaryService.getSendUserData(token);
        UserInfoDetail receiveUserData = diaryService.getReceiveUserData(userCode.getUserCode());
        Diary diary = diaryService.getDiary(diaryID);

        long sendUserID = sendUserData.getUserID();
        long receiveUserID = receiveUserData.getUserID();

        if(sendUserID == receiveUserID) throw new BusinessLogicException(BusinessLogicException.of.SELF_INVITE_EXCEPTION);
        int sendUserDiaryStatus = diaryService.getUserDiaryStatus(sendUserID,diaryID);
        int receiveUserDiaryStatus = diaryService.getUserDiaryStatus(receiveUserID,diaryID);

        // 현재 유저가 다이어리에 존재하지 않을 경우 불가능하므로 예외 리턴
        if(sendUserDiaryStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);

        // 현재 유저가 다이어리에 존재할 경우
        else if(sendUserDiaryStatus == 100){

            // 상대방 유저가 다이어리에 존재하지 않을 경우 다이어리 초대 진행
            if(receiveUserDiaryStatus == 404){
                diaryService.inviteDiary(sendUserData,receiveUserData,diary);
                return new SuccessResponse.Builder(SuccessResponse.of.INVITE_DIARY_SUCCESS).build().getResponse();
            }

            // 상대방 유저가 이미 초대를 받은 상태일 경우, 또는 이미 초대를 수락한 경우 예외 리턴
            else if(receiveUserDiaryStatus == 200) throw new BusinessLogicException(BusinessLogicException.of.ALREADY_INVITE_EXCEPTION);
            else throw new BusinessLogicException(BusinessLogicException.of.EXIST_USER_DIARY_EXCEPTION);
        }

        // 현재 유저가 다이어리 초대를 받은 경우 항상 다이어리 수락 진행
        else{
            List<UserDiary> acceptableDiaryList = diaryService.getAcceptableDiaryList(sendUserID,diaryID);
            diaryService.acceptDiary(acceptableDiaryList, receiveUserID, diary.getStatus());
            diaryService.setFcmAndLogToAcceptDiary(acceptableDiaryList, sendUserData, diary);
            return new SuccessResponse.Builder(SuccessResponse.of.ACCEPT_DIARY_SUCCESS).build().getResponse();
        }
    }

    //12-1. 유저에게 온 다이어리 초대 요청 조회 API
    @GetMapping("/log/{diaryID}")
    public Map<String, ?> getInviteList(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        DiaryRequestOfUser getRequestList = diaryService.getRequestOfUser(diaryService.getUserID(token),diaryID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",getRequestList)
                .build().getResponse();
    }

    //13. 다이어리 퇴장 및 초대 거절 API
    @DeleteMapping("/diary/{diaryID}")
    public Map<String, ?> deleteDiary(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        long userID = diaryService.getUserID(token);
        int userDiaryStatus = diaryService.getUserDiaryStatus(userID,diaryID);

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
    public Map<String, ?> updateDiary(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateDiary updateDiary,
            BindingResult bindingResult
    ){
        long userID = diaryService.getUserID(token);
        int userDiaryStatus = diaryService.getUserDiaryStatus(userID,updateDiary.getDiary());

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
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="page", required = true) int page,
            @RequestParam(name="status", required = true) int status,
            @RequestParam(name="keyword", required = false) String keyword
    ){
        List<DiaryListResponse> res = keyword==null ?
                diaryService.getDiaryList(diaryService.getUserID(token), status, page) :
                diaryService.getDiaryListWithKeyword(diaryService.getUserID(token), status, page, keyword);

        // 다이어리가 존재하지 않을 경우 메시지 출력
        if(res.isEmpty()){
            Map<String,String> emptyRes = new HashMap<>();
            emptyRes.put("message","등록된 다이어리가 없습니다.");
            return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",emptyRes)
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
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID,
            @RequestParam(name="page", required = true) int page,
            @RequestParam(name="status", required = true) int status
    ){
        long userID = diaryService.getUserID(token);
        int userDiaryStatus = diaryService.getUserDiaryStatus(userID,diaryID);

        // 현재 다이어리에 속해 있는 경우 조회 진행
        if(userDiaryStatus == 100){
            List<DiaryMemberListResponse> res = diaryService.getDiaryMemberList(diaryID, status, page);

            // 다이어리가 존재하지 않을 경우 메시지 출력
            if(res.isEmpty()){
                Map<String,String> emptyRes = new HashMap<>();
                emptyRes.put("message","등록된 친구가 없습니다.");
                return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                        .add("result",emptyRes)
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

    //15-3. 다이어리 공지 수정 API
    @PatchMapping("/notice")
    public Map<String, ?> updateNotice(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateNotice updateNotice,
            BindingResult bindingResult
    ){
        long userID = diaryService.getUserID(token);
        diaryService.updateNotice(userID, updateNotice.getDiary(), updateNotice.getNotice());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_DIARY_NOTICE_SUCCESS).build().getResponse();
    }

    //15-4. 다이어리 공지 조회 API
    @GetMapping("/notice/{diaryID}")
    public Map<String, ?> getNotice(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID
    ){
        long userID = diaryService.getUserID(token);
        int userDiaryStatus = diaryService.getUserDiaryStatus(userID,diaryID);

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
