package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UserCode;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        long diaryID = diaryService.createDiary(createDiary.getTitle(), status);
        diaryService.setUserDiary(userID, diaryID, createDiary.getTitle(), status);
        diaryService.setDiaryNotice(userID, diaryID, "");
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

//    //12-1. 유저에게 온 다이어리 초대 요청 조회 API
//    @GetMapping("/log/{diaryID}")
//    public Map<String, ?> getInviteList(
//            @RequestHeader(TokenProvider.HEADER_NAME) String token,
//            @PathVariable("diaryID") long diaryID
//    ){
//
//    }

    // $r->addRoute('GET', '/log/{diaryID:\d+}', ['DiaryController', 'getRequestByUserCode']);                                 //12-1. 유저에게 온 다이어리 초대 요청 조회 API
    // $r->addRoute('DELETE', '/diary/{diaryID:\d+}', ['DiaryController', 'deleteDiary']);
    // $r->addRoute('PATCH', '/diary', ['DiaryController', 'updateDiary']);
    // $r->addRoute('GET', '/diaries', ['DiaryController', 'getDiaries']);                                                     //15. 다이어리 조회 API
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/users', ['DiaryController', 'getDiariesMember']);                           //15-0. 다이어리 멤버 조회 API

    // $r->addRoute('PATCH', '/notice', ['NoticeController', 'updateNotice']);                                                 //15-3. 다이어리 공지 수정 API
    // $r->addRoute('GET', '/notice/{diaryID:\d+}', ['NoticeController', 'getNotice']);                                        //15-4. 다이어리 공지 조회 API
}
