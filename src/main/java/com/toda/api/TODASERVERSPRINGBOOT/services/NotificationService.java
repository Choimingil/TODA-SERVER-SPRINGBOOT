package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmTokenVer2;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmByDevice;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.FcmAllowedResponse;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("notificationService")
public class NotificationService extends AbstractService implements BaseService {
    private final NotificationRepository notificationRepository;

    public NotificationService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            NotificationRepository notificationRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void saveFcmToken(long userID, int status, String fcm, String allowable){
        notificationRepository.deleteByUserIDAndStatus(userID,0);
        notificationRepository.save(Notification.builder()
                .userID(userID)
                .fcm(fcm)
                .isAllowed(allowable)
                .isEventAllowed(allowable)
                .isRemindAllowed(allowable)
                .status(status)
                .build());
    }

    public Notification getNotification(long userID, String fcm){
        Notification notification = notificationRepository.findByUserIDAndFcmAndStatusNot(userID,fcm,0);
        if(notification == null){
            // 알림 토큰 추가 오류 대비 토큰 추가 작업 진행
            if(fcm != null){
                saveFcmToken(userID,100,fcm,"Y");
                saveFcmToken(userID,200,fcm,"Y");
            }
            else throw new NoArgException(NoArgException.of.NULL_PARAM_EXCEPTION);
        }
        return notification;
    }

    public List<FcmByDevice> getFcmByDevice(long userID){
        List<Notification> notificationList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(userID,"Y",0);
        return notificationList.stream().map(
                element -> FcmByDevice.builder()
                    .token(element.getFcm())
                    .device(element.getStatus())
                    .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public boolean updateFcmAllowed(Notification notification, int status) {
        String isCurrentAllowed;
        switch (status) {
            case 0 -> {
                isCurrentAllowed = notification.getIsAllowed().equals("Y") ? "N" : "Y";
                notification.setIsAllowed(isCurrentAllowed);
            }
            case 1 -> {
                isCurrentAllowed = notification.getIsRemindAllowed().equals("Y") ? "N" : "Y";
                notification.setIsRemindAllowed(isCurrentAllowed);
            }
            case 2 -> {
                isCurrentAllowed = notification.getIsEventAllowed().equals("Y") ? "N" : "Y";
                notification.setIsEventAllowed(isCurrentAllowed);
            }
            default -> throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        }

        notificationRepository.save(notification);
        return isCurrentAllowed.equals("Y");
    }

    @Transactional
    public void updateFcmTime(Notification notification, String time){
        notification.setTime(time);
        notificationRepository.save(notification);
    }
}
