package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateFile;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateStatus;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateFcmAllowed;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateFcmTime;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NotificationController extends AbstractController implements BaseController {
    private final NotificationService notificationService;

    public NotificationController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, NotificationService notificationService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt);
        this.notificationService = notificationService;
    }

    //1-5. 알림 토큰 저장 API
    @PostMapping("/notification")
    @SetMdcBody
    public Map<String, ?> saveFcmToken(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="type", required = false) String type,
            @RequestBody @Valid SaveFcmToken saveFcmToken,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int status = type==null ? 100 : (type.equals("2") ? 200 : 100);
        notificationService.saveFcmToken(userID,status,saveFcmToken);
        return new SuccessResponse.Builder(SuccessResponse.of.SAVE_FCM_TOKEN_SUCCESS)
                .build().getResponse();
    }

    ///7-5. 알림 허용 여부 확인 API(3개)
    @GetMapping("/alarm/ver2")
    public Map<String, ?> getNotificationAllowed(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="fcmToken") String fcm
    ){
        long userID = getUserID(token);
        Notification notification = notificationService.getNotification(userID,fcm);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("isBasicAllowed",notification.getIsAllowed())
                .add("isRemindAllowed",notification.getIsRemindAllowed())
                .add("isEventAllowed",notification.getIsEventAllowed())
                .build().getResponse();
    }

    //7-6. 알림 허용 여부 변경 API(3개)
    @PatchMapping("/alarm/ver2")
    @SetMdcBody
    public Map<String, ?> updateNotificationAllowed(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdateFcmAllowed updateFcmAllowed,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        boolean updateNotificationAllowed = notificationService.updateFcmAllowed(userID, updateFcmAllowed.getFcmToken(), updateFcmAllowed.getAlarmType());
        if(updateNotificationAllowed) return new SuccessResponse.Builder(SuccessResponse.of.DO_FCM_ALLOWED_SUCCESS).build().getResponse();
        else return new SuccessResponse.Builder(SuccessResponse.of.UNDO_FCM_ALLOWED_SUCCESS).build().getResponse();
    }

    //7-7. 알림 시간 조회 API
    @GetMapping("/alarm/time")
    public Map<String, ?> getNotificationTime(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="fcmToken") String fcm
    ){
        long userID = getUserID(token);
        Notification notification = notificationService.getNotification(userID,fcm);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",notification.getTime())
                .build().getResponse();
    }

    //7-8. 알림 시간 변경 API
    @PatchMapping("/alarm/time")
    @SetMdcBody
    public Map<String, ?> updateNotificationTime(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdateFcmTime updateFcmTime,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        notificationService.updateFcmTime(userID, updateFcmTime.getFcmToken(), updateFcmTime.getTime());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_FCM_TIME_SUCCESS).build().getResponse();
    }
}
