package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmMap;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserFcm;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FcmTokenProvider extends RedisProvider implements BaseProvider {
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final NotificationRepository notificationRepository;

    /**
     * 인터셉터에서 Redis에 토큰이 없을 경우 등록
     * @param userID
     */
    public void checkFcmExist(long userID){
        getFcmMap(userID);
    }

    /**
     * Redis에 저장된 토큰 중 입력받은 토큰의 아이디 가져옴
     * @param userID
     * @param fcm
     * @return
     */
    public long getNotificationID(long userID, String fcm){
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

    public Map<String,Integer> getTokenStatus(long userID){
        FcmMap fcmMap = getFcmMap(userID);
        return fcmMap.getTokenStatus();
    }

    /**
     * 유저 리스트를 받아 한번에 전송할 FCM 리스트 가져오기
     * Redis에 값이 없을 경우 null 리턴, 사용하는 클래스에서 예외 처리 해줘야 함
     * @param userID
     * @return
     */
    public FcmGroup getSingleUserFcmList(long userID){
        List<String> aosFcmList = new ArrayList<>();
        List<String> iosFcmList = new ArrayList<>();

        Map<String,Integer> tokenStatus = getTokenStatus(userID);
        for(String fcm : tokenStatus.keySet()){
            int status = tokenStatus.get(fcm);
            if(status == 100) iosFcmList.add(fcm);
            else if(status == 200) aosFcmList.add(fcm);
        }

        return FcmGroup.builder()
                .aosFcmList(aosFcmList)
                .iosFcmList(iosFcmList)
                .build();
    }

    /**
     * 유저 리스트를 받아 한번에 전송할 FCM 리스트 가져오기
     * Redis에 값이 없을 경우 빈 리스트 리턴
     * @param userIDList
     * @return
     */
    public FcmGroup getMultiUserFcmList(List<Long> userIDList){
        List<FcmMap> fcmMapList = getFcmMapList(userIDList);
        List<String> aosFcmList = new ArrayList<>();
        List<String> iosFcmList = new ArrayList<>();

        if(fcmMapList == null) return FcmGroup.builder()
                .aosFcmList(aosFcmList)
                .iosFcmList(iosFcmList)
                .build();

        for(FcmMap fcmMap : fcmMapList){
            Map<String,Long> tokenIDList = fcmMap.getTokenIDs();
            Map<String,Integer> tokenStatusList = fcmMap.getTokenStatus();
            for(String fcm : tokenStatusList.keySet()){
                int status = tokenStatusList.get(fcm);
                if(status == 100) iosFcmList.add(fcm);
                else if(status == 200) aosFcmList.add(fcm);
            }
        }

        return FcmGroup.builder()
                .aosFcmList(aosFcmList)
                .iosFcmList(iosFcmList)
                .build();
    }

//    public List<String> getMultiUserFcmList(List<Long> userIDList){
//        List<FcmMap> fcmMapList = getFcmMapList(userIDList);
//        if(fcmMapList == null) return new ArrayList<>();
//
//
//
//        Set<String> fcmSet = new HashSet<>();
//        for(FcmMap fcmMap : fcmMapList) fcmSet.addAll(getFcmSet(fcmMap));
//        return new ArrayList<>(fcmSet);
//    }

    private Set<String> getFcmSet(FcmMap fcmMap){
        Map<String, Long> tokenIDs = fcmMap.getTokenIDs();
        Map<String, Integer> tokenStatus = fcmMap.getTokenStatus();
        Set<String> fcmSet = tokenIDs.keySet();
        fcmSet.addAll(tokenStatus.keySet());
        return fcmSet;
    }

    /**
     * 새로운 토큰 하나를 추가
     * @param userID
     * @param notificationID
     * @param fcm
     */
    public void setNewFcm(long userID, String fcm, long notificationID, int status){
        FcmMap fcmMap = getFcmMap(userID);
        Map<String,Long> tokenIDs = new HashMap<>(fcmMap.getTokenIDs());
        if(!tokenIDs.containsKey(fcm)) tokenIDs.put(fcm,notificationID);
        else tokenIDs.replace(fcm,notificationID);

        Map<String,Integer> tokenStatus = new HashMap<>(fcmMap.getTokenStatus());
        if(!tokenStatus.containsKey(fcm)) tokenStatus.put(fcm,status);
        else tokenStatus.replace(fcm,status);

        setUserFcm(userID,FcmMap.builder().tokenIDs(tokenIDs).tokenStatus(tokenStatus).build());
    }

    /**
     * 특정 토큰 하나를 제거
     * @param userID
     * @param fcm
     */
    public void deleteFcm(long userID, String fcm){
        FcmMap fcmMap = getFcmMap(userID);
        Map<String,Long> tokenIDs = new HashMap<>(fcmMap.getTokenIDs());
        Map<String,Integer> tokenStatus = new HashMap<>(fcmMap.getTokenStatus());
        tokenIDs.remove(fcm);
        tokenStatus.remove(fcm);
        setUserFcm(userID,FcmMap.builder().tokenIDs(tokenIDs).tokenStatus(tokenStatus).build());
    }

    /**
     * Redis에 값이 없다면 DB 접속해서 값 최신화
     * @param userID
     * @return
     */
    private FcmMap getFcmMap(long userID){
        FcmMap userFcmMap = convertRedisToFcmMap(userID);
        if(userFcmMap == null){
            List<UserFcm> userFcmList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(userID,"Y",0);
            Map<String,Long> tokenIDs = convertUserFcmListToTokenIDs(userFcmList);
            Map<String,Integer> tokenStatus = convertUserFcmListToTokenStatus(userFcmList);
            userFcmMap = FcmMap.builder()
                    .tokenIDs(tokenIDs)
                    .tokenStatus(tokenStatus)
                    .build();
            setUserFcm(userID, userFcmMap);
        }
        return userFcmMap;
    }

    /**
     * Redis에 저장된 FCM 맵 2개 리턴(토큰 아이디, 토큰 상태)
     * @param userID
     * @return
     */
    private FcmMap convertRedisToFcmMap(long userID) {
        try {
            byte[] byteCode = getRedis(getKey(userID)).get();
            if(byteCode == null) return null;

            UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.parseFrom(byteCode);
            Map<String,Long> tokenIDs = userFcm.getTokenIDMap();
            Map<String,Integer> tokenStatus = userFcm.getTokenStatusMap();
            return FcmMap.builder()
                    .tokenIDs(tokenIDs)
                    .tokenStatus(tokenStatus)
                    .build();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    private Map<String,Long> convertUserFcmListToTokenIDs(List<UserFcm> userFcmList){
        return userFcmList.stream()
                .collect(Collectors.toMap(UserFcm::getFcm, UserFcm::getNotificationID));
    }

    private Map<String,Integer> convertUserFcmListToTokenStatus(List<UserFcm> userFcmList){
        return userFcmList.stream()
                .collect(Collectors.toMap(UserFcm::getFcm, UserFcm::getStatus));
    }

    /**
     * Redis에 저장된 여러 유저 토큰 정보 한번에 리스트로 조회
     * @param userIDList
     * @return
     */
    private List<FcmMap> getFcmMapList(List<Long> userIDList) {
        try {
            List<String> keys = userIDList.stream()
                    .map(this::getKey)
                    .toList();

            List<byte[]> bytes = getRedisList(keys).get();
            if(bytes == null) return null;

            return bytes.stream().map(element -> {
                try{
                    UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.parseFrom(element);
                    Map<String,Long> tokenIDs = userFcm.getTokenIDMap();
                    Map<String,Integer> tokenStatus = userFcm.getTokenStatusMap();
                    return FcmMap.builder()
                            .tokenIDs(tokenIDs)
                            .tokenStatus(tokenStatus)
                            .build();
                }
                catch (InvalidProtocolBufferException e){
                    throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
                }
            }).collect(Collectors.toList());
        }
        catch (ExecutionException | InterruptedException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 유저 Fcm 정보 리스트 전체를 Redis에 저장
     * @param userID
     * @param fcmMap
     */
    private void setUserFcm(long userID, FcmMap fcmMap){
        UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.newBuilder()
                .putAllTokenID(fcmMap.getTokenIDs())
                .putAllTokenStatus(fcmMap.getTokenStatus())
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
