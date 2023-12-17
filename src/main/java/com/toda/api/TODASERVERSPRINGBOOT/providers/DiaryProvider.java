package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.DiaryNotice;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class DiaryProvider extends AbstractProvider implements BaseProvider {
    private final KafkaProducerProvider kafkaProducerProvider;
    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final UserLogRepository userLogRepository;
    private final DiaryNoticeRepository diaryNoticeRepository;

    @Transactional
    public void addUserDiary(long userID, long diaryID, String diaryName, int status){
        UserDiary userDiary = new UserDiary();
        userDiary.setUserID(userID);
        userDiary.setDiaryID(diaryID);
        userDiary.setDiaryName(diaryName);
        userDiary.setStatus(status);
        userDiaryRepository.save(userDiary);
    }

    @Transactional
    public void addDiaryNotice(long userID, long diaryID, String notice){
        DiaryNotice diaryNotice = new DiaryNotice();
        diaryNotice.setUserID(userID);
        diaryNotice.setDiaryID(diaryID);
        diaryNotice.setNotice(notice);
        diaryNoticeRepository.save(diaryNotice);
    }

    @Transactional
    public void addUserLog(long sendUserID, long receiveUserID, long diaryID, int type, int status){
        UserLog userLog = new UserLog();
        userLog.setReceiveID(receiveUserID);
        userLog.setType(type);
        userLog.setTypeID(diaryID);
        userLog.setSendID(sendUserID);
        userLog.setStatus(status);
        userLogRepository.save(userLog);
    }









    /*
    Getter
     */

    /**
     * 다이어리 상태값 유효성 검증
     * 1 : 함께 쓰는 다이어리
     * 2 : 혼자 쓰는 다이어리
     * 3 : 즐겨 찾는 다이어리
     * @param origin
     * @param newStatus
     */
    public void checkDiaryStatus(UserDiary origin, int newStatus){
        int currDiaryStatus = origin.getStatus()%100;
        int newDiaryStatus = newStatus%100;
        if( (currDiaryStatus == 1 && newDiaryStatus == 2) || (currDiaryStatus == 2 && newDiaryStatus == 1) || (currDiaryStatus == newDiaryStatus))
            throw new BusinessLogicException(BusinessLogicException.of.WRONG_DIARY_STATUS_EXCEPTION);

        // 현재 상태값이 3일 경우 원본 다이어리의 상태값을 적용하여 검증
        if(currDiaryStatus == 3){
            if(newDiaryStatus != origin.getDiary().getStatus())
                throw new BusinessLogicException(BusinessLogicException.of.WRONG_DIARY_STATUS_EXCEPTION);
        }
    }

    public int getDiaryColorCode(Set<DiaryColors> colorSet, int color){
        StringBuilder sb = new StringBuilder();
        sb.append("CODE_").append(color);
        String key = sb.toString();
        if(!colorSet.contains(DiaryColors.valueOf(key)))
            throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return Integer.parseInt(DiaryColors.valueOf(key).code);
    }

    public List<UserDiary> getAcceptableDiaryList(long userID, long diaryID){
        List<UserDiary> res = new ArrayList<>();
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryID(userID,diaryID);
        for(UserDiary userDiary : userDiaryList) if((userDiary.getStatus()%10) == 0) res.add(userDiary);
        return res;
    }

    public UserData getSendUserData(String token){
        return tokenProvider.decodeToken(token);
    }

    public UserInfoDetail getReceiveUserData(String userCode){
        return userRepository.getUserDataByUserCode(userCode);
    }

    public Diary getDiary(long diaryID){
        Diary diary = diaryRepository.findByDiaryID(diaryID);
        if(diary == null) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_EXCEPTION);
        if(diary.getStatus()%100 == 2) throw new BusinessLogicException(BusinessLogicException.of.ALONE_DIARY_INVITATION_EXCEPTION);
        return diary;
    }









    /*
    FCM 관련 메서드
     */
    public void sendFcm(
            long receiveUserID,
            String title,
            String body,
            int typeNum,
            long diaryID,
            FcmGroup fcmGroup
    ){
        KafkaFcmProto.KafkaFcmRequest params = KafkaFcmProto.KafkaFcmRequest.newBuilder()
                .setUserID(receiveUserID)
                .setTitle(title)
                .setBody(body)
                .setTypeNum(typeNum)
                .setDataID(diaryID)
                .addAllAosFcm(fcmGroup.getAosFcmList())
                .addAllIosFcm(fcmGroup.getIosFcmList())
                .build();

        try{
            System.out.println(params.toString());
            kafkaProducerProvider.getKafkaProducer("fcm", params).get();
        }
        catch (InterruptedException | ExecutionException e){
            throw new WrongAccessException(WrongAccessException.of.SEND_FCM_EXCEPTION);
        }
    }
}
