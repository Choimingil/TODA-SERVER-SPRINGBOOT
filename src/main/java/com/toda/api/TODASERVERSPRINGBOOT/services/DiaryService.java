package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryRequestOfUser;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmTokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.KafkaProducerProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component("diaryService")
@RequiredArgsConstructor
public class DiaryService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final UserLogRepository userLogRepository;
    private final DiaryNoticeRepository diaryNoticeRepository;
    private final TokenProvider tokenProvider;
    private final FcmTokenProvider fcmTokenProvider;
    private final KafkaProducerProvider kafkaProducerProvider;
    private final Set<DiaryColors> colorSet = EnumSet.allOf(DiaryColors.class);
    private final Set<DiaryStatus> statusSet = EnumSet.allOf(DiaryStatus.class);

    @Transactional
    public long createDiary(String diaryName, int status){
        Diary diary = new Diary();
        diary.setDiaryName(diaryName);
        diary.setStatus(status);
        Diary newDiary = diaryRepository.save(diary);
        return newDiary.getDiaryID();
    }

    @Transactional
    public void setUserDiary(long userID, long diaryID, String diaryName, int status){
        UserDiary userDiary = new UserDiary();
        userDiary.setUserID(userID);
        userDiary.setDiaryID(diaryID);
        userDiary.setDiaryName(diaryName);
        userDiary.setStatus(status);
        userDiaryRepository.save(userDiary);
    }

    @Transactional
    public void setDiaryNotice(long userID, long diaryID, String notice){
        DiaryNotice diaryNotice = new DiaryNotice();
        diaryNotice.setUserID(userID);
        diaryNotice.setDiaryID(diaryID);
        diaryNotice.setNotice(notice);
        diaryNoticeRepository.save(diaryNotice);
    }

    @Transactional
    public void inviteDiary(UserData sendUserData, UserInfoDetail receiveUserData, Diary diary){
        long sendUserID = sendUserData.getUserID();
        long receiveUserID = receiveUserData.getUserID();
        long diaryID = diary.getDiaryID();
        String diaryName = diary.getDiaryName();
        int status = (int)(sendUserID*10);

        // 이미 다이어리를 탈퇴한 기록이 있는 경우 해당 값을 초대값으로 변경
        List<UserDiary> deleteList = userDiaryRepository.findByUserIDAndDiaryIDAndStatus(receiveUserID,diaryID,999);
        if(!deleteList.isEmpty()){
            boolean isEdited = false;
            for(UserDiary userDiary : deleteList){
                if(!isEdited){
                    userDiary.setStatus((int)(sendUserID*10));
                    userDiaryRepository.save(userDiary);
                    isEdited = true;
                }
                else userDiaryRepository.delete(userDiary);
            }
        }
        else setUserDiary(receiveUserID,diaryID,diaryName,status);

        // 로그 추가
        setUserLog(sendUserID,receiveUserID,diaryID,1,100);

        // 초대 FCM 전송
        String title = getFcmTitle(receiveUserData.getUserName());                                              // "To. ".$receivename."님";
        String body = getFcmBodyInvite(sendUserData.getUserName(), sendUserData.getUserCode(), diaryName);      // $sendname."님(".$usercode.")이 ".$diaryname."에 초대합니다:)";
        FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(receiveUserID);
        sendFcm(receiveUserID, title, body, 1, diaryID, fcmGroup);
    }

    @Transactional
    public void acceptDiary(List<UserDiary> acceptableDiaryList, long receiveUserID, int status){
        // receiveUserID에 해당하는 요청만 수락하고, 나머지 요청은 삭제
        if(!acceptableDiaryList.isEmpty()){
            for(UserDiary userDiary : acceptableDiaryList){
                int originStatus = userDiary.getStatus();
                if(originStatus/10 == receiveUserID){
                    userDiary.setStatus(status);
                    userDiaryRepository.save(userDiary);

                    // 초대 완료 로그 추가
                    setUserLog(userDiary.getUserID(),receiveUserID,userDiary.getDiaryID(),1,999);

                    // 리스트 값을 이전값으로 원상복구
                    userDiary.setStatus(originStatus);
                }
                else userDiaryRepository.delete(userDiary);
            }
        }
    }

    @Transactional
    public void setFcmAndLogToAcceptDiary(List<UserDiary> acceptableDiaryList, UserData sendUserData, Diary diary){
        // 초대를 보낸 모든 유저들 로그에 모두 초대 완료 설정
        Set<Long> receiveUserIDList = new HashSet<>();
        for(UserDiary userDiary : acceptableDiaryList){
            long receiveUserID = userDiary.getStatus()/10;
            receiveUserIDList.add(receiveUserID);
        }

        // FCM 받을 유저들 아이디를 기준으로 이름값 가져오기
        List<User> receiveUserData = userRepository.findByUserIDIn(receiveUserIDList);

        // 푸시 알림 발송 메시지 세팅
        String body = getFcmBodyAccept(sendUserData.getUserName(), sendUserData.getUserCode(), diary.getDiaryName());      // $sendname."님(".$usercode.")이 ".$diaryname." 초대에 수락하셨습니다:)";
        for(User receiveUser : receiveUserData){
            // 상대방 유저가 다이어리에 존재할 경우 FCM 메시지 발송 및 로그 체크
            int userDiaryStatus = getUserDiaryStatus(receiveUser.getUserID(),diary.getDiaryID());
            if(userDiaryStatus == 100){
                setUserLog(receiveUser.getUserID(),sendUserData.getUserID(),diary.getDiaryID(),2,100);
                String title = getFcmTitle(receiveUser.getUserName());                                              // "To. ".$receivename."님";
                FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(receiveUser.getUserID());
                sendFcm(receiveUser.getUserID(), title, body, 2, diary.getDiaryID(), fcmGroup);
            }
        }
    }

    public DiaryRequestOfUser getRequestOfUser(long userID, long diaryID){
        List<DiaryRequestOfUser> requestList = userDiaryRepository.getDiaryRequestOfUser(diaryID,userID);
        return requestList.get(0);
    }

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

    public int getDiaryStatus(int status, int color){
        if(status<1 || status>statusSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_STATUS_EXCEPTION);
        if(color<1 || color>colorSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return color*100 + status;
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
        if(getDiaryStatus(diary.getStatus()) == 2) throw new BusinessLogicException(BusinessLogicException.of.ALONE_DIARY_INVITATION_EXCEPTION);
        return diary;
    }

    public long getUserID(String token){
        return tokenProvider.getUserID(token);
    }

    private void sendFcm(
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

    @Transactional
    private void setUserLog(long sendUserID, long receiveUserID, long diaryID, int type, int status){
        UserLog userLog = userLogRepository.findByReceiveIDAndTypeID(receiveUserID,diaryID);
        if(userLog == null){
            userLog = new UserLog();
            userLog.setReceiveID(receiveUserID);
            userLog.setType(type);
            userLog.setTypeID(diaryID);
            userLog.setSendID(sendUserID);
        }
        userLog.setStatus(status);
        userLogRepository.save(userLog);
    }

    private String getFcmTitle(String userName){
        return new StringBuilder()
                .append("To. ")
                .append(userName)
                .append("님")
                .toString();
    }

    private String getFcmBodyInvite(String userName, String userCode, String diaryName){
        return new StringBuilder()
                .append(userName)
                .append("님(")
                .append(userCode)
                .append(")이 ")
                .append(diaryName)
                .append("에 초대합니다:)")
                .toString();
    }

    private String getFcmBodyAccept(String userName, String userCode, String diaryName){
        return new StringBuilder()
                .append(userName)
                .append("님(")
                .append(userCode)
                .append(")이 ")
                .append(diaryName)
                .append(" 초대에 수락하셨습니다:)")
                .toString();
    }

    private String getDiaryColorCode(int color){
        StringBuilder sb = new StringBuilder();
        sb.append("CODE_").append(color);
        String key = sb.toString();
        if(!colorSet.contains(DiaryColors.valueOf(key)))
            throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return DiaryColors.valueOf(key).code;
    }

    private int getDiaryStatus(int status){
        return status%10;
    }
}
