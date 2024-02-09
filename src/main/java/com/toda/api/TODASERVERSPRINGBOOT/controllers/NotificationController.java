package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmTokenVer2;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateFcmAllowed;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateFcmTime;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.FcmAllowedResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NotificationController extends AbstractController implements BaseController {
    private final NotificationService notificationService;

    public NotificationController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, NotificationService notificationService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.notificationService = notificationService;
    }

    //1-6. 알림 토큰 저장 API
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
        notificationService.saveFcmToken(userID,status,saveFcmToken.getToken(),saveFcmToken.getIsAllowed());
        return new SuccessResponse.Builder(SuccessResponse.of.SAVE_FCM_TOKEN_SUCCESS)
                .build().getResponse();
    }

    //1-7. 알림 토큰 저장 API Ver2
    @PostMapping("/notification/ver2")
    @SetMdcBody
    public Map<String, ?> saveFcmTokenVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid SaveFcmTokenVer2 saveFcmToken,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int status = saveFcmToken.getType()==1 ? 100 : 200;
        notificationService.saveFcmToken(userID,status,saveFcmToken.getToken(),saveFcmToken.getIsAllowed());
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
        if(notification == null){
            // IOS 알림 토큰 추가 오류 대비 토큰 추가 작업 진행
            if(fcm != null) notificationService.saveFcmToken(userID,100,fcm,"Y");
            else throw new NoArgException(NoArgException.of.NULL_PARAM_EXCEPTION);
            
            return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result", FcmAllowedResponse.builder()
                            .isBasicAllowed(true)
                            .isRemindAllowed(true)
                            .isEventAllowed(true)
                            .build())
                    .build().getResponse();
        }
        else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result", FcmAllowedResponse.builder()
                        .isBasicAllowed(notification.getIsAllowed().equals("Y"))
                        .isRemindAllowed(notification.getIsRemindAllowed().equals("Y"))
                        .isEventAllowed(notification.getIsEventAllowed().equals("Y"))
                        .build())
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
        Notification notification = notificationService.getNotification(userID,updateFcmAllowed.getFcmToken());
        boolean updateNotificationAllowed = notificationService.updateFcmAllowed(notification, updateFcmAllowed.getAlarmType());
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
        Notification notification = notificationService.getNotification(userID,updateFcmTime.getFcmToken());
        if(notification.getIsRemindAllowed().equals("N")) throw new WrongArgException(WrongArgException.of.WRONG_REMIND_FCM_EXCEPTION);
        notificationService.updateFcmTime(notification, updateFcmTime.getTime());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_FCM_TIME_SUCCESS).build().getResponse();
    }
}
