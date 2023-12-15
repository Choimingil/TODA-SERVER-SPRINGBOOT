package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.DiaryNotice;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryList;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
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
    public final Set<DiaryColors> colorSet = EnumSet.allOf(DiaryColors.class);
    public final Set<DiaryStatus> statusSet = EnumSet.allOf(DiaryStatus.class);

    public interface CustomParams<T>{
        void method(T params);
    }

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

    @Transactional
    public <T> void updateList(List<T> entityList, CustomParams<T> params, JpaRepository<T, Long> repository) {
        if (!entityList.isEmpty()) {
            boolean isEdited = false;
            for (T entity : entityList) {
                if (!isEdited) {
                    params.method(entity);
                    repository.save(entity);
                    isEdited = true;
                } else {
                    repository.delete(entity);
                }
            }
        }
    }

    @Transactional
    public void updateUserLog(long receiveUserID, long diaryID, int type, int status){
        List<UserLog> userLogList = userLogRepository.findByReceiveIDAndTypeAndTypeIDAndStatusNot(receiveUserID,type,diaryID,999);
        for(UserLog userLog : userLogList){
            userLog.setStatus(status);
            userLogRepository.save(userLog);
        }
    }

    public long getTimeDiffSec(LocalDateTime currentDateTime, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(targetDateTime, currentDateTime);
        return duration.getSeconds();
    }







    /*
    Getter
     */

    /**
     * 유저가 다이어리에 어떤 상태로 존재하는지 확인
     * @param userID
     * @param diaryID
     * @return 404,100,200
     * 404 : 유저가 다이어리에 속하지 않을 경우
     * 100 : 유저가 다이어리에 속할 경우
     * 200 : 유저가 다이어리에 속하지 않고 초대 요청이 온 경우
     */
    public int getUserDiaryStatus(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        if(userDiaryList.isEmpty()) return 404;
        for(UserDiary userDiary : userDiaryList) if(userDiary.getStatus()%10 != 0) return 100;
        return 200;
    }

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

    public int getDiaryStatus(int status, int color){
        if(status<1 || status>statusSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_STATUS_EXCEPTION);
        if(color<1 || color>colorSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return color*100 + status;
    }

    public int getDiaryColorCode(int color){
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

    public long getUserID(String token){
        return tokenProvider.getUserID(token);
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
            kafkaProducerProvider.getKafkaProducer("fcm", params).get();
        }
        catch (InterruptedException | ExecutionException e){
            throw new WrongAccessException(WrongAccessException.of.SEND_FCM_EXCEPTION);
        }
    }

    public String getFcmTitle(String userName){
        return new StringBuilder()
                .append("To. ")
                .append(userName)
                .append("님")
                .toString();
    }

    public String getFcmBodyInvite(String userName, String userCode, String diaryName){
        return new StringBuilder()
                .append(userName)
                .append("님(")
                .append(userCode)
                .append(")이 ")
                .append(diaryName)
                .append("에 초대합니다:)")
                .toString();
    }

    public String getFcmBodyAccept(String userName, String userCode, String diaryName){
        return new StringBuilder()
                .append(userName)
                .append("님(")
                .append(userCode)
                .append(")이 ")
                .append(diaryName)
                .append(" 초대에 수락하셨습니다:)")
                .toString();
    }
}
