package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("notificationService")
@RequiredArgsConstructor
public class NotificationService extends AbstractService implements BaseService {
    private final NotificationRepository notificationRepository;
    private final TokenProvider tokenProvider;
    private final FcmProvider fcmProvider;

    @Transactional
    public void saveFcmToken(String jwt, int status, SaveFcmToken saveFcmToken){
        long userID = tokenProvider.getUserID(jwt);
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
        fcmProvider.setNewFcm(userID, fcm, newNotification.getNotificationID(), status);
    }

    public Notification getNotification(String jwt, String fcm){
        long userID = tokenProvider.getUserID(jwt);
        long notificationID = fcmProvider.getNotificationID(userID,fcm);
        return notificationRepository.findByNotificationID(notificationID);
    }

    @Transactional
    public boolean updateFcmAllowed(String jwt, String fcm, int status) {
        long userID = tokenProvider.getUserID(jwt);
        long notificationID = fcmProvider.getNotificationID(userID, fcm);
        Notification notification = notificationRepository.findByNotificationID(notificationID);

        String curr;
        switch (status) {
            case 0 -> {
                curr = notification.getIsAllowed().equals("Y") ? "N" : "Y";
                notification.setIsAllowed(curr);
                if (curr.equals("Y")) fcmProvider.setNewFcm(
                        notification.getUserID(),
                        notification.getFcm(),
                        notification.getNotificationID(),
                        notification.getStatus()
                );
                else fcmProvider.deleteFcm(notification.getUserID(), notification.getFcm());
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
        long userID = tokenProvider.getUserID(jwt);
        long notificationID = fcmProvider.getNotificationID(userID, fcm);
        if(notificationRepository.existsByNotificationIDAndIsRemindAllowed(notificationID,"N"))
            throw new WrongArgException(WrongArgException.of.WRONG_REMIND_FCM_EXCEPTION);

        notificationRepository.updateFcmTime(time,notificationID);
    }
}
