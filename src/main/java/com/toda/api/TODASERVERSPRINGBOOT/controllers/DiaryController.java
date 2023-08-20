package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.GetUserCode;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.DiaryService;
import com.toda.api.TODASERVERSPRINGBOOT.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        int status = diaryService.getDiaryStatus(createDiary.getStatus(), createDiary.getColor());
        long diaryID = diaryService.createDiary(createDiary.getTitle(), status);
        diaryService.setUserDiary(token, diaryID, createDiary.getTitle(), status);
        diaryService.setDiaryNotice(token, diaryID, "");
        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_DIARY_SUCCESS)
                .build().getResponse();
    }



    // $r->addRoute('POST', '/diaries/{diaryID:\d+}/user', ['DiaryController', 'addDiaryFriend']);                             //12. 다이어리 유저 추가 API
    // $r->addRoute('GET', '/log/{diaryID:\d+}', ['DiaryController', 'getRequestByUserCode']);                                 //12-1. 유저에게 온 다이어리 초대 요청 조회 API
    // $r->addRoute('DELETE', '/diary/{diaryID:\d+}', ['DiaryController', 'deleteDiary']);
    // $r->addRoute('PATCH', '/diary', ['DiaryController', 'updateDiary']);
    // $r->addRoute('GET', '/diaries', ['DiaryController', 'getDiaries']);                                                     //15. 다이어리 조회 API
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/users', ['DiaryController', 'getDiariesMember']);                           //15-0. 다이어리 멤버 조회 API

    // $r->addRoute('PATCH', '/notice', ['NoticeController', 'updateNotice']);                                                 //15-3. 다이어리 공지 수정 API
    // $r->addRoute('GET', '/notice/{diaryID:\d+}', ['NoticeController', 'getNotice']);                                        //15-4. 다이어리 공지 조회 API
}
