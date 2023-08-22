package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateFcmAllowed;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateFcmTime;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Notification;
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
    public Map<String, ?> getNotificationAllowed(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="fcmToken") String fcm
    ){
        Notification notification = notificationService.getNotification(token,fcm);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("isBasicAllowed",notification.getIsAllowed())
                .add("isRemindAllowed",notification.getIsRemindAllowed())
                .add("isEventAllowed",notification.getIsEventAllowed())
                .build().getResponse();
    }

    //7-6. 알림 허용 여부 변경 API(3개)
    @PatchMapping("/alarm/ver2")
    public Map<String, ?> updateNotificationAllowed(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateFcmAllowed updateFcmAllowed,
            BindingResult bindingResult
    ){
        boolean updateNotificationAllowed = notificationService.updateFcmAllowed(token, updateFcmAllowed.getFcmToken(), updateFcmAllowed.getAlarmType());
        if(updateNotificationAllowed) return new SuccessResponse.Builder(SuccessResponse.of.DO_FCM_ALLOWED_SUCCESS).build().getResponse();
        else return new SuccessResponse.Builder(SuccessResponse.of.UNDO_FCM_ALLOWED_SUCCESS).build().getResponse();
    }

    //7-7. 알림 시간 조회 API
    @GetMapping("/alarm/time")
    public Map<String, ?> getNotificationTime(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="fcmToken") String fcm
    ){
        Notification notification = notificationService.getNotification(token,fcm);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",notification.getTime())
                .build().getResponse();
    }

    //7-8. 알림 시간 변경 API
    @PatchMapping("/alarm/time")
    public Map<String, ?> updateNotificationTime(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateFcmTime updateFcmTime,
            BindingResult bindingResult
    ){
        notificationService.updateFcmTime(token, updateFcmTime.getFcmToken(), updateFcmTime.getTime());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_FCM_TIME_SUCCESS).build().getResponse();
    }
}
