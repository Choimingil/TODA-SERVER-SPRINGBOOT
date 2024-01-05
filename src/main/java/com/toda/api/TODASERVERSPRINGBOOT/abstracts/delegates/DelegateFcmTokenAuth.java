package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractAuth;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseFcmTokenAuth;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserFcm;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmMap;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public final class DelegateFcmTokenAuth extends AbstractAuth implements BaseFcmTokenAuth {
    private final DelegateRedis delegateRedis;
    private final NotificationRepository notificationRepository;

    public DelegateFcmTokenAuth(
            DelegateJwt delegateJwt,
            DelegateMdc delegateMdc,
            DelegateRedis delegateRedis,
            NotificationRepository notificationRepository
    ) {
        super(delegateJwt, delegateMdc);
        this.delegateRedis = delegateRedis;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public long getNotificationID(long userID, String fcm) {
        FcmMap fcmMap = getFcmMap(userID);
        Map<String,Long> tokenIDs = fcmMap.getTokenIDs();
        if(tokenIDs.containsKey(fcm)) return tokenIDs.get(fcm);
        else{
            if(notificationRepository.existsByUserIDAndFcmAndStatusNot(userID,fcm,0)){
                UserFcm userFcm = notificationRepository.findByUserIDAndFcmAndIsAllowedAndStatusNot(userID,fcm,"Y",0);
                long notificationID = userFcm.getNotificationID();
                setNewFcm(userID,fcm,notificationID,userFcm.getStatus());
                return notificationID;
            }
            else throw new NoArgException(NoArgException.of.NO_FCM_EXCEPTION);
        }
    }

    @Override
    public Map<String, Integer> getTokenStatus(long userID) {
        FcmMap fcmMap = getFcmMap(userID);
        return fcmMap.getTokenStatus();
    }

    @Override
    public void setNewFcm(long userID, String fcm, long notificationID, int status) {
        FcmMap fcmMap = getFcmMap(userID);
        Map<String,Long> tokenIDs = new HashMap<>(fcmMap.getTokenIDs());
        if(!tokenIDs.containsKey(fcm)) tokenIDs.put(fcm,notificationID);
        else tokenIDs.replace(fcm,notificationID);

        Map<String,Integer> tokenStatus = new HashMap<>(fcmMap.getTokenStatus());
        if(!tokenStatus.containsKey(fcm)) tokenStatus.put(fcm,status);
        else tokenStatus.replace(fcm,status);

        setUserFcm(userID,FcmMap.builder().tokenIDs(tokenIDs).tokenStatus(tokenStatus).build());
    }

    @Override
    public void setFcmMap(long userID) {
        setUserFcm(userID, getFcmMapWithDb(userID));
    }

    @Override
    public FcmMap convertRedisToFcmMap(long userID) {
        return delegateRedis.convertRedisData(getKey(userID), UserFcmProto.UserFcm.class, userFcm -> FcmMap.builder()
                .tokenIDs(userFcm.getTokenIDMap())
                .tokenStatus(userFcm.getTokenStatusMap())
                .build());
    }

    /**
     * Redis에 값이 없다면 DB 접속해서 값 최신화
     * @param userID
     * @return
     */
    private FcmMap getFcmMap(long userID){
        FcmMap userFcmMap = convertRedisToFcmMap(userID);
        if(userFcmMap == null){
            userFcmMap = getFcmMapWithDb(userID);
            setUserFcm(userID,userFcmMap);
        }
        return userFcmMap;
    }

    /**
     * DB에 접근해서 FcmToken값 가져온 후 FcmMap 형식으로 변환
     * @param userID
     * @return
     */
    private FcmMap getFcmMapWithDb(long userID){
        List<UserFcm> userFcmList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(userID,"Y",0);
        Map<String,Long> tokenIDs = userFcmList.stream().collect(Collectors.toMap(UserFcm::getFcm, UserFcm::getNotificationID));
        Map<String,Integer> tokenStatus = userFcmList.stream().collect(Collectors.toMap(UserFcm::getFcm, UserFcm::getStatus));
        return FcmMap.builder().tokenIDs(tokenIDs).tokenStatus(tokenStatus).build();
    }

    /**
     * 유저 Fcm 정보 리스트 전체를 Redis에 저장
     * Redis에 저장될 키 : {userID}_Tokens
     * @param userID
     * @param fcmMap
     */
    private void setUserFcm(long userID, FcmMap fcmMap){
        UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.newBuilder()
                .putAllTokenID(fcmMap.getTokenIDs())
                .putAllTokenStatus(fcmMap.getTokenStatus())
                .build();
        delegateRedis.setRedis(getKey(userID), userFcm.toByteArray());
    }

    /**
     * Redis에 저장될 키 생성 : {userID}_Tokens
     * @param userID
     * @return
     */
    private String getKey(long userID){
        return new StringBuilder().append(userID).append("_Tokens").toString();
    }
}
