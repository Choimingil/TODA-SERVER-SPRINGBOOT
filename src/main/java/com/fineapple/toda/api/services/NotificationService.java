package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.exceptions.NoArgException;
import com.fineapple.toda.api.exceptions.WrongArgException;
import com.fineapple.toda.api.entities.Notification;
import com.fineapple.toda.api.models.dtos.FcmByDevice;
import com.fineapple.toda.api.repositories.NotificationRepository;
import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
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
        List<Notification> sameFcmNotificationList = notificationRepository.findByFcm(fcm);
        for(Notification notification : sameFcmNotificationList) notificationRepository.delete(notification);

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
        List<Notification> notificationList = notificationRepository.findByUserIDAndFcmAndStatusNot(userID,fcm,0);
        if(notificationList.isEmpty()){
            // IOS 알림 토큰 추가 오류 대비 토큰 추가 작업 진행
            if(fcm != null) saveFcmToken(userID,100,fcm,"Y");
            else throw new NoArgException(NoArgException.of.NULL_PARAM_EXCEPTION);
        }

        return notificationList.get(0);
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
