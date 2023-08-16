package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.SaveFcmToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("notificationService")
@RequiredArgsConstructor
public class NotificationService extends AbstractService implements BaseService {
    private final NotificationRepository notificationRepository;
    private final TokenProvider tokenProvider;
    private final FcmProvider fcmProvider;

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
        fcmProvider.setNewFcm(userID, newNotification.getNotificationID(), fcm);
    }

//    public int getNotificationStatus(String jwt, String fcm){
//        long userID = tokenProvider.getUserID(jwt);
//        if(!notificationRepository.existsByUserIDAndStatusNot(userID,0))
//            throw new NoArgException(NoArgException.of.NO_FCM_EXCEPTION);
//
//
//    }
}
