package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AnnouncementController extends AbstractController implements BaseController {
    private final AnnouncementService announcementService;

    public AnnouncementController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, AnnouncementService announcementService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.announcementService = announcementService;
    }

    //38. 공지사항 리스트 조회 API
    @GetMapping("/announcement")
    public Map<String, ?> getAnnouncement(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        long userID = getUserID(token);
        List<Map<String,Object>> announcementList = announcementService.getAnnouncement(userID,page);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",announcementList)
                .build().getResponse();
    }

    //39. 공지사항 상세 조회 API
    @GetMapping("/announcement/{announcementID}")
    public Map<String, ?> getAnnouncementDetail(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("announcementID") String id
    ){
        long userID = getUserID(token);
        long announcementID = Long.parseLong(id);
        List<Map<String,Object>> announcementDetails = announcementService.getAnnouncementDetail(userID,announcementID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",announcementDetails.get(0))
                .build().getResponse();
    }

    //40. 공지사항 읽었는지 안읽었는지 확인 API
    @GetMapping("/announcement/check")
    public Map<String, ?> checkAnnouncement(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ){
        long userID = getUserID(token);
        boolean isAllAnnouncementRead = announcementService.isAllAnnouncementRead(userID);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",isAllAnnouncementRead)
                .build().getResponse();
    }



    // $r->addRoute('GET', '/popup/{version}', ['LoginController', 'getPopupRead']);                                           //1-9. 업데이트 공지 읽었는지 확인 API
    // $r->addRoute('PATCH', '/popup/{version}', ['LoginController', 'updatePopupRead']);                                      //1-10. 업데이트 공지 읽기 API
}
