package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.MethodParamsInterface;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserLogRepository;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class AbstractFcm extends AbstractRedis implements BaseService, MethodParamsInterface {
    /**
     * Kafka로 발송할 프로토콜 버퍼 세팅
     * FCM 받을 유저의 FCM 토큰을 저장
     * @param sendID
     * @param check
     * @param params
     * @param fcmDto
     */
    protected void setKafkaTopicFcm(
            long sendID,
            CheckParams2Params<Long,String> check,
            FcmMethod<Long,String> params,
            FcmDto fcmDto
    ){
        List<String> aosFcmList = new ArrayList<>();
        List<String> iosFcmList = new ArrayList<>();

        for(Map.Entry<Long,String> entry : fcmDto.getMap().entrySet()){
            long userID = entry.getKey();
            String userName = entry.getValue();

            // 발신자와 수신자가 같을 경우 알림 미발송
            if(userID == sendID) continue;

            // 상대방 유저가 FCM 수신 조건 만족 시 발송
            if(check.check(userID,userName)){
                FcmGroup fcmGroup = params.method(userID,userName);
                aosFcmList.addAll(fcmGroup.getAosFcmList());
                iosFcmList.addAll(fcmGroup.getIosFcmList());
            }
        }

        try{
            KafkaFcmProto.KafkaFcmRequest request = KafkaFcmProto.KafkaFcmRequest.newBuilder()
                    .setTitle(fcmDto.getTitle())
                    .setBody(fcmDto.getBody())
                    .setTypeNum(fcmDto.getTypeNum())
                    .setDataID(fcmDto.getDataID())
                    .addAllAosFcm(aosFcmList)
                    .addAllIosFcm(iosFcmList)
                    .build();
            fcmDto.getProvider().getKafkaProducer("fcm", request).get();
        }
        catch (InterruptedException | ExecutionException e){
            throw new WrongAccessException(WrongAccessException.of.SEND_FCM_EXCEPTION);
        }
    }

    /**
     * setKafkaTopicFcm 메서드의 파리미터 (Map)
     * <유저 아이디, 유저 닉네임>
     * @param check
     * @param method
     * @param entityList
     * @return
     * @param <T>
     */
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


    /**
     * 유저 로그 추가
     * @param userLogRepository
     * @param sendUserID
     * @param receiveUserID
     * @param diaryID
     * @param type
     * @param status
     */
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


    /**
     * FCM 타이틀 설정
     * @return
     */
    protected String getFcmTitle(){
        return "투다에서 알림이 왔어요!";
    }

    /**
     * FCM 본문 설정
     * @param userName
     * @param userCode
     * @param objName
     * @param type
     * @return
     */
    protected String getFcmBody(String userName, String userCode, String objName, int type){
        return switch (type) {
            case 1 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append("에 초대합니다:)").toString();
            case 2 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append(" 초대에 수락하셨습니다:)").toString();
            case 3 -> new StringBuilder().append(userName).append("님이 일기를 남겼습니다:)").toString();
            case 4 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append("님의 일기를 좋아합니다:)").toString();
            case 5 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 댓글을 남겼습니다:)").toString();
            case 6 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 대댓글을 남겼습니다:)").toString();
            default -> throw new WrongAccessException(WrongAccessException.of.FCM_BODY_EXCEPTION);
        };
    }

}
