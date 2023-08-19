package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserFcm;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FcmProvider extends RedisProvider implements BaseProvider {
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final NotificationRepository notificationRepository;

    /**
     * 인터셉터에서 Redis에 토큰이 없을 경우 등록
     * @param userID
     */
    public void checkFcmExist(long userID){
        getUserFcmMap(userID);
    }

    /**
     * Redis에 저장된 토큰 중 입력받은 토큰의 아이디 가져옴
     * @param userID
     * @param fcm
     * @return
     */
    public long getNotificationID(long userID, String fcm){
        Map<String,Long> userFcmMap = getUserFcmMap(userID);
        if(userFcmMap.containsKey(fcm)) return userFcmMap.get(fcm);
        else{
            if(notificationRepository.existsByUserIDAndFcmAndStatusNot(userID,fcm,0)){
                UserFcm userFcm = notificationRepository.findByUserIDAndFcmAndIsAllowedAndStatusNot(userID,fcm,"Y",0);
                long notificationID = userFcm.getNotificationID();
                setNewFcm(userID, notificationID, fcm);
                return notificationID;
            }
            else throw new NoArgException(NoArgException.of.NO_FCM_EXCEPTION);
        }
    }

    /**
     * 새로운 토큰 하나를 추가
     * @param userID
     * @param notificationID
     * @param fcm
     */
    public void setNewFcm(long userID, long notificationID, String fcm){
        Map<String,Long> userFcmMap = new HashMap<>(getUserFcmMap(userID));
        if(!userFcmMap.containsKey(fcm)){
            userFcmMap.put(fcm,notificationID);
            setUserFcm(userID, userFcmMap);
        }
    }

    /**
     * 특정 토큰 하나를 제거
     * @param userID
     * @param fcm
     */
    public void deleteFcm(long userID, String fcm){
        Map<String,Long> userFcmMap = new HashMap<>(getUserFcmMap(userID));
        if(userFcmMap.containsKey(fcm)){
            userFcmMap.remove(fcm);
            setUserFcm(userID, userFcmMap);
        }
    }

    /**
     * Redis에 값이 없다면 DB 접속해서 값 최신화
     * @param userID
     * @return
     */
    private Map<String,Long> getUserFcmMap(long userID){
        Map<String,Long> userFcmMap = getUserFcm(userID);
        if(userFcmMap == null){
            List<UserFcm> userFcmList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(userID,"Y",0);
            userFcmMap = convertUserFcm(userFcmList);
            setUserFcm(userID, userFcmMap);
        }
        return userFcmMap;
    }

    /**
     * Redis에 저장된 유저 토큰 정보 조회
     * @param userID
     * @return
     */
    private Map<String,Long> getUserFcm(long userID) {
        try {
            byte[] byteCode = getRedis(getKey(userID)).get();
            if(byteCode == null) return null;

            UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.parseFrom(byteCode);
            return userFcm.getTokensMap();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 유저 Fcm 정보 리스트 전체를 Redis에 저장
     * @param userID
     * @param userFcmMap
     */
    private void setUserFcm(long userID, Map<String,Long> userFcmMap){
        UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.newBuilder()
                .putAllTokens(userFcmMap)
                .build();
        setRedis(getKey(userID), userFcm.toByteArray());
    }

    /**
     * Redis에 저장되어 있는 유저 토큰 정보 삭제
     * @param userID
     */
    public void deleteUserFcm(long userID){
        deleteRedis(getKey(userID));
    }

    /**
     * List<UserFcm> to Map<String,Long> converter
     * @param userFcmList
     * @return
     */
    private Map<String,Long> convertUserFcm(List<UserFcm> userFcmList){
        return userFcmList.stream()
                .collect(Collectors.toMap(UserFcm::getFcm, UserFcm::getNotificationID));
    }

    /**
     * Redis에 저장될 키 생성 : {userID}_Tokens
     * @param userID
     * @return
     */
    private String getKey(long userID){
        StringBuilder sb = new StringBuilder();
        sb.append(userID).append("_Tokens");
        return sb.toString();
    }

    /**
     * redisTemplate getter
     * @return
     */
    @Override
    protected RedisTemplate<String, byte[]> getRedisTemplate() {
        return redisTemplate;
    }
}
