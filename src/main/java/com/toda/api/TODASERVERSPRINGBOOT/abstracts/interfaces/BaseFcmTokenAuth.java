package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmMap;

import java.util.Map;

public interface BaseFcmTokenAuth {
    /**
     * 아직 사용 안함
     * 유저 리스트를 받아 한번에 전송할 FCM 리스트 가져오기
     * Redis에 값이 없을 경우 null 리턴, 사용하는 클래스에서 예외 처리 해줘야 함
     * @param userID
     * @return
     */
    FcmGroup getUserFcmTokenList(long userID);
    /**
     * Redis에 저장된 토큰 중 입력받은 토큰의 아이디 가져옴
     * @param userID
     * @param fcm
     * @return
     */
    long getNotificationID(long userID, String fcm);

    /**
     * FcmMap에서 FcmToken 값 가져오기
     * IOS, AOS, 만료 토큰 여부
     * <토큰, status> 형태
     * @param userID
     * @return
     */
    Map<String,Integer> getTokenStatus(long userID);

    /**
     * 새로운 토큰 하나를 추가
     * @param userID
     * @param notificationID
     * @param fcm
     */
    void setNewFcm(long userID, String fcm, long notificationID, int status);


    /**
     * Redis에 값이 없다면 DB 접속해서 값 최신화
     * @param userID
     * @return
     */
    void setFcmMap(long userID);

    /**
     * Redis에 저장된 FCM 맵 2개 리턴(토큰 아이디, 토큰 상태)
     * @param userID
     * @return
     */
    FcmMap convertRedisToFcmMap(long userID);

    /**
     * 특정 토큰 하나를 제거
     * @param userID
     * @param fcm
     */
    void deleteFcm(long userID, String fcm);



//    /**
//     * 인터셉터에서 Redis에 토큰이 없을 경우 등록
//     * @param userID
//     */
//    public void checkFcmExist(long userID){
//        getFcmMap(userID);
//    }
//    /**
//     * Redis에 저장되어 있는 유저 토큰 정보 삭제
//     * @param userID
//     */
//    public void deleteUserFcm(long userID){
//        deleteRedis(getKey(userID));
//    }
}
