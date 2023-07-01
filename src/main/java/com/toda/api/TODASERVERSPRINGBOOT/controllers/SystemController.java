package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.ValidateEmail;
import com.toda.api.TODASERVERSPRINGBOOT.services.SystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SystemController extends AbstractController implements BaseController {
    private final SystemService systemService;
 
    // 1-2. 이메일 중복 확인 API
    @PostMapping("/email/valid")
    @SetMdcBody
    public Map<String, ?> validateEmail(
            @RequestBody @Valid ValidateEmail validateEmail,
            BindingResult bindingResult
    ) {
        if(systemService.isValidEmail(validateEmail.getEmail()))
            return new SuccessResponse.Builder(SuccessResponse.of.VALIDATE_EMAIL_SUCCESS).build().getResponse();
        else return new FailResponse.Builder(FailResponse.of.EXIST_EMAIL_EXCEPTION).build().getResponse();
    }

    // $r->addRoute('GET', '/update', ['LoginController', 'checkUpdate']);                                                     //1-6. 강제 업데이트 API
    // $r->addRoute('GET', '/popup/{version}', ['LoginController', 'getPopupRead']);                                           //1-9. 업데이트 공지 읽었는지 확인 API
    // $r->addRoute('PATCH', '/popup/{version}', ['LoginController', 'updatePopupRead']);                                      //1-10. 업데이트 공지 읽기 API
    // $r->addRoute('POST', '/email/check', ['LoginController', 'isMyEmail']);                                                 //1-11. 자신의 이메일인지 확인 API
    // $r->addRoute('GET', '/terms', ['LoginController', 'getTerms']);                                                         //1-12. 약관 조회 API



    // $r->addRoute('GET', '/announcement', ['LoginController', 'getAnnouncement']);                                           //38. 공지사항 리스트 조회 API
    // $r->addRoute('GET', '/announcement/{announcementID:\d+}', ['LoginController', 'getAnnouncementDetail']);                //39. 공지사항 상세 조회 API
    // $r->addRoute('GET', '/announcement/check', ['LoginController', 'getAnnouncementCheck']);
}
