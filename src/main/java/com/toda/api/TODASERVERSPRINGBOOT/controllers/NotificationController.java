package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.GetAppPassword;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController extends AbstractController implements BaseController {
    private final NotificationService notificationService;

    //1-5. 알림 토큰 저장 API
    @PostMapping("/notification")
    public Map<String, ?> saveFcmToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="type", required = false) String type,
            @RequestBody @Valid SaveFcmToken saveFcmToken,
            BindingResult bindingResult
    ){
        int status = type==null ? 100 : (type.equals("2") ? 200 : 100);
        notificationService.saveFcmToken(token,status,saveFcmToken);
        return new SuccessResponse.Builder(SuccessResponse.of.SAVE_FCM_TOKEN_SUCCESS)
                .build().getResponse();
    }

    ///7-5. 알림 허용 여부 확인 API(3개)
    @GetMapping("/alarm/ver2")
    public Map<String, ?> getNotificationStatus(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="fcmToken") String fcm
    ){


        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .build().getResponse();
    }


    // $r->addRoute('GET', '/alarm/ver2', ['NotificationController', 'checkAlarmVer2']);                                       //7-5. 알림 허용 여부 확인 API(3개)
    // $r->addRoute('PATCH', '/alarm/ver2', ['NotificationController', 'updateAlarmVer2']);                                    //7-6. 알림 허용 여부 변경 API(3개)
    // $r->addRoute('GET', '/alarm/time', ['NotificationController', 'getAlarmTime']);                                         //7-7. 알림 시간 조회 API
    // $r->addRoute('PATCH', '/alarm/time', ['NotificationController', 'updateAlarmTime']);                                    //7-8. 알림 시간 변경 API

    // $r->addRoute('GET', '/alarm', ['NotificationController', 'checkAlarm']);                                                //1-7. 알림 허용 여부 확인 API
    // $r->addRoute('PATCH', '/alarm', ['NotificationController', 'updateAlarm']);                                             //1-8. 알림 허용 여부 변경 API
}
