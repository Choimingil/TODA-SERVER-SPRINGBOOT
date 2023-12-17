package com.toda.api.TODASERVERSPRINGBOOT.services.base;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmTokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.KafkaProducerProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class AbstractFcmService extends AbstractService implements BaseService{
    protected void sendFcmForSingleUser(
            CheckParams2Params<Long,String> check,
            MethodParams2Params<Long,String> params,
            Map<Long,String> map
    ){
        for(Map.Entry<Long,String> entry : map.entrySet()){
            long userID = entry.getKey();
            String userName = entry.getValue();

            // 상대방 유저가 FCM 수신 조건 만족 시 발송
            if(check.check(userID,userName)) params.method(userID,userName);
        }
    }

    protected <T> Map<Long,String> getFcmReceiveUserMap(
            CheckParams2Params<T, Map<Long,String>> check,
            MethodParams2Params<T, Map<Long,String>> method,
            List<T> entityList
    ){
        Map<Long,String> receiveUserMap = new HashMap<>();
        for(T entity : entityList){
            if(check.check(entity, receiveUserMap))
                method.method(entity, receiveUserMap);
        }
        return receiveUserMap;
    }

//    // 로그 추가
//        diaryProvider.addUserLog(sendUserID,receiveUserID,diaryID,1,100);
//
//    // 초대 FCM 전송
//    String title = diaryProvider.getFcmTitle(receiveUserData.getUserName());                                              // "To. ".$receivename."님";
//    String body = diaryProvider.getFcmBodyInvite(sendUserData.getUserName(), sendUserData.getUserCode(), diaryName);      // $sendname."님(".$usercode.")이 ".$diaryname."에 초대합니다:)";
//    FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(receiveUserID);
//        diaryProvider.sendFcm(receiveUserID, title, body, 1, diaryID, fcmGroup);


//    // 초대를 보낸 모든 유저들 로그에 모두 초대 완료 설정
//    Map<Long,String> receiveUserMap = new HashMap<>();
//        for(
//    UserDiary userDiary : acceptableDiaryList){
//        long receiveUserID = userDiary.getStatus()/10;
//        receiveUserMap.put(receiveUserID,userDiary.getUser().getUserName());
//    }
//
//    // 푸시 알림 발송 메시지 세팅
//    String body = diaryProvider.getFcmBodyAccept(sendUserData.getUserName(), sendUserData.getUserCode(), diary.getDiaryName());      // $sendname."님(".$usercode.")이 ".$diaryname." 초대에 수락하셨습니다:)";
//    for(Map.Entry<Long, String> entry : receiveUserMap.entrySet()){
//        long userID = entry.getKey();
//        String userName = entry.getValue();
//
//        // 상대방 유저가 다이어리에 존재할 경우 FCM 메시지 발송 및 로그 체크
//        int userDiaryStatus = getUserDiaryStatus(userID,diary.getDiaryID());
//        if(userDiaryStatus == 100){
//            diaryProvider.addUserLog(userID,sendUserData.getUserID(),diary.getDiaryID(),2,100);
//            String title = diaryProvider.getFcmTitle(userName);                                              // "To. ".$receivename."님";
//            FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(userID);
//            diaryProvider.sendFcm(userID, title, body, 2, diary.getDiaryID(), fcmGroup);
//        }
//    }

//    public void sendFcm(
//            long receiveUserID,
//            String title,
//            String body,
//            int typeNum,
//            long diaryID,
//            FcmGroup fcmGroup
//    ){
//        KafkaFcmProto.KafkaFcmRequest params = KafkaFcmProto.KafkaFcmRequest.newBuilder()
//                .setUserID(receiveUserID)
//                .setTitle(title)
//                .setBody(body)
//                .setTypeNum(typeNum)
//                .setDataID(diaryID)
//                .addAllAosFcm(fcmGroup.getAosFcmList())
//                .addAllIosFcm(fcmGroup.getIosFcmList())
//                .build();
//
//        try{
//            kafkaProducerProvider.getKafkaProducer("fcm", params).get();
//        }
//        catch (InterruptedException | ExecutionException e){
//            throw new WrongAccessException(WrongAccessException.of.SEND_FCM_EXCEPTION);
//        }
//    }

//    private Map<String,Integer> getTokenStatus(long userID){
//        FcmMap fcmMap = getFcmMap(userID);
//        return fcmMap.getTokenStatus();
//    }

//    /**
//     * Redis에 값이 없다면 DB 접속해서 값 최신화
//     * @param userID
//     * @return
//     */
//    private FcmMap getFcmMap(long userID){
//        FcmMap userFcmMap = convertRedisToFcmMap(userID);
//        if(userFcmMap == null){
//            List<UserFcm> userFcmList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(userID,"Y",0);
//            Map<String,Long> tokenIDs = convertUserFcmListToTokenIDs(userFcmList);
//            Map<String,Integer> tokenStatus = convertUserFcmListToTokenStatus(userFcmList);
//            userFcmMap = FcmMap.builder()
//                    .tokenIDs(tokenIDs)
//                    .tokenStatus(tokenStatus)
//                    .build();
//            setUserFcm(userID, userFcmMap);
//        }
//        return userFcmMap;
//    }

//    /**
//     * 유저 Fcm 정보 리스트 전체를 Redis에 저장
//     * @param userID
//     * @param fcmMap
//     */
//    private void setUserFcm(long userID, FcmMap fcmMap){
//        UserFcmProto.UserFcm userFcm = UserFcmProto.UserFcm.newBuilder()
//                .putAllTokenID(fcmMap.getTokenIDs())
//                .putAllTokenStatus(fcmMap.getTokenStatus())
//                .build();
//        setRedis(getKey(userID), userFcm.toByteArray());
//    }

    protected String getFcmTitle(String userName){
        return new StringBuilder()
                .append("To. ")
                .append(userName)
                .append("님")
                .toString();
    }

    protected String getFcmBody(String userName, String userCode, String diaryName, int type){
        return switch (type) {
            case 1 ->
                    new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(diaryName).append("에 초대합니다:)").toString();
            case 2 ->
                    new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(diaryName).append(" 초대에 수락하셨습니다:)").toString();
            case 3 -> new StringBuilder().append(userName).append("님이 일기를 남겼습니다:)").toString();
            default -> throw new WrongAccessException(WrongAccessException.of.FCM_BODY_EXCEPTION);
        };
    }

}
