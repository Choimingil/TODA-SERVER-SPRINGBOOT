package com.toda.api.TODASERVERSPRINGBOOT.services.base;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserLogRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class AbstractFcmService extends AbstractService implements BaseService{
    protected void setKafkaTopicFcm(
            CheckParams2Params<Long,String> check,
            FcmMethod<Long,String> params,
            FcmDto fcmDto
    ){
        List<String> aosFcmList = new ArrayList<>();
        List<String> iosFcmList = new ArrayList<>();

        for(Map.Entry<Long,String> entry : fcmDto.getMap().entrySet()){
            long userID = entry.getKey();
            String userName = entry.getValue();

            // 상대방 유저가 FCM 수신 조건 만족 시 발송
            if(check.check(userID,userName)){
                FcmGroup fcmGroup = params.method(userID,userName);
                aosFcmList.addAll(fcmGroup.getAosFcmList());
                iosFcmList.addAll(fcmGroup.getIosFcmList());
            }
        }

        try{
            KafkaFcmProto.KafkaFcmRequest request = KafkaFcmProto.KafkaFcmRequest.newBuilder()
                    // 유저 아이디 부분 테스트 이후 제거하기
                    .setUserID(1L)
                    .setTitle(fcmDto.getTitle())
                    .setBody(fcmDto.getBody())
                    .setTypeNum(fcmDto.getTypeNum())
                    .setDataID(fcmDto.getDataID())
                    .addAllAosFcm(aosFcmList)
                    .addAllIosFcm(iosFcmList)
                    .build();

            logger.info(request.toString());
            fcmDto.getProvider().getKafkaProducer("fcm", request).get();
        }
        catch (InterruptedException | ExecutionException e){
            throw new WrongAccessException(WrongAccessException.of.SEND_FCM_EXCEPTION);
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

















    @Transactional
    public void addUserLog(UserLogRepository userLogRepository, long sendUserID, long receiveUserID, long diaryID, int type, int status){
        UserLog userLog = new UserLog();
        userLog.setReceiveID(receiveUserID);
        userLog.setType(type);
        userLog.setTypeID(diaryID);
        userLog.setSendID(sendUserID);
        userLog.setStatus(status);
        userLogRepository.save(userLog);
    }


















//    protected String getFcmTitle(String userName){
//        return new StringBuilder()
//                .append("To. ")
//                .append(userName)
//                .append("님")
//                .toString();
//  }
    protected String getFcmTitle(){
        return "투다에서 알림이 왔어요!";
    }

    protected String getFcmBody(String userName, String userCode, String objName, int type){
        return switch (type) {
            case 1 ->
                    new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append("에 초대합니다:)").toString();
            case 2 ->
                    new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append(" 초대에 수락하셨습니다:)").toString();
            case 3 -> new StringBuilder().append(userName).append("님이 일기를 남겼습니다:)").toString();
            default -> throw new WrongAccessException(WrongAccessException.of.FCM_BODY_EXCEPTION);
        };
    }

}
