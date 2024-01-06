package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateKafka delegateKafka,
            NotificationRepository notificationRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateKafka);
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void saveFcmToken(String jwt, int status, SaveFcmToken saveFcmToken){
        long userID = getUserID(jwt);
        String fcm = saveFcmToken.getToken();
        String allowable = saveFcmToken.getIsAllowed();

        notificationRepository.deleteByUserIDAndStatus(userID,0);
        Notification newNotification = notificationRepository.save(Notification.builder()
                .userID(userID)
                .fcm(fcm)
                .isAllowed(allowable)
                .isEventAllowed(allowable)
                .isRemindAllowed(allowable)
                .status(status)
                .build());
        setNewFcm(userID, fcm, newNotification.getNotificationID(), status);
    }

    public Notification getNotification(String jwt, String fcm){
        long userID = getUserID(jwt);
        long notificationID = getNotificationID(userID,fcm);
        return notificationRepository.findByNotificationID(notificationID);
    }

    @Transactional
    public boolean updateFcmAllowed(String jwt, String fcm, int status) {
        long userID = getUserID(jwt);
        long notificationID = getNotificationID(userID, fcm);
        Notification notification = notificationRepository.findByNotificationID(notificationID);

        String curr;
        switch (status) {
            case 0 -> {
                curr = notification.getIsAllowed().equals("Y") ? "N" : "Y";
                notification.setIsAllowed(curr);
                if (curr.equals("Y")) setNewFcm(
                        notification.getUserID(),
                        notification.getFcm(),
                        notification.getNotificationID(),
                        notification.getStatus()
                );
                else deleteFcm(notification.getUserID(), notification.getFcm());
            }
            case 1 -> {
                curr = notification.getIsRemindAllowed().equals("Y") ? "N" : "Y";
                notification.setIsRemindAllowed(curr);
            }
            case 2 -> {
                curr = notification.getIsEventAllowed().equals("Y") ? "N" : "Y";
                notification.setIsEventAllowed(curr);
            }
            default -> throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        }

        notificationRepository.save(notification);
        return curr.equals("Y");
    }

    @Transactional
    public void updateFcmTime(String jwt, String fcm, String time){
        long userID = getUserID(jwt);
        long notificationID = getNotificationID(userID, fcm);
        if(notificationRepository.existsByNotificationIDAndIsRemindAllowed(notificationID,"N"))
            throw new WrongArgException(WrongArgException.of.WRONG_REMIND_FCM_EXCEPTION);

        notificationRepository.updateFcmTime(time,notificationID);
    }
}
