package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface BaseFcm {
    /**
     * Kafka로 발송할 프로토콜 버퍼 세팅
     * FCM 받을 유저의 FCM 토큰을 저장
     * @param sendID
     * @param check
     * @param fcmGroup
     * @param fcmDto
     */
    void setKafkaTopicFcm(long sendID, BiFunction<Long,String,Boolean> check, BiFunction<Long,String,FcmGroup> fcmGroup, FcmDto fcmDto);

    /**
     * setKafkaTopicFcm 메서드의 파리미터 (Map)
     * <유저 아이디, 유저 닉네임>
     * @param check
     * @param run
     * @param entityList
     * @return
     * @param <T>
     */
    <T> Map<Long,String> getFcmReceiveUserMap(BiFunction<T,Map<Long,String>,Boolean> check, BiConsumer<T, Map<Long,String>> run, List<T> entityList);


    /**
     * 유저 로그 추가
     * @param sendUserID
     * @param receiveUserID
     * @param diaryID
     * @param type
     * @param status
     */
    void addUserLog(long sendUserID, long receiveUserID, long diaryID, int type, int status);

    /**
     * FCM 타이틀 설정
     * @return
     */
    String getFcmTitle();

    /**
     * FCM 본문 설정
     * @param userName
     * @param userCode
     * @param objName
     * @param type
     * @return
     */
    String getFcmBody(String userName, String userCode, String objName, int type);
}
