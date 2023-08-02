package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AnnouncementController extends AbstractController implements BaseController {
    private final AnnouncementService announcementService;
    private final TokenProvider tokenProvider;

    //38. 공지사항 리스트 조회 API
    @GetMapping("/announcement")
    public Map<String, ?> getAnnouncement(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        long userID = tokenProvider.getUserID(token);
        List<Map<String,Object>> announcementList = announcementService.getAnnouncement(userID,page);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",announcementList)
                .build().getResponse();
    }

    //39. 공지사항 상세 조회 API
    @GetMapping("/announcement/{announcementID}")
    public Map<String, ?> getAnnouncementDetail(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("announcementID") String id
    ){
        long userID = tokenProvider.getUserID(token);
        long announcementID = Long.parseLong(id);
        List<Map<String,Object>> announcementDetails = announcementService.getAnnouncementDetail(userID,announcementID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",announcementDetails.get(0))
                .build().getResponse();
    }

    //40. 공지사항 읽었는지 안읽었는지 확인 API
    @GetMapping("/announcement/check")
    public Map<String, ?> checkAnnouncement(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ){
        long userID = tokenProvider.getUserID(token);
        boolean isAllAnnouncementRead = announcementService.isAllAnnouncementRead(userID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",isAllAnnouncementRead)
                .build().getResponse();
    }



    // $r->addRoute('GET', '/popup/{version}', ['LoginController', 'getPopupRead']);                                           //1-9. 업데이트 공지 읽었는지 확인 API
    // $r->addRoute('PATCH', '/popup/{version}', ['LoginController', 'updatePopupRead']);                                      //1-10. 업데이트 공지 읽기 API
}
