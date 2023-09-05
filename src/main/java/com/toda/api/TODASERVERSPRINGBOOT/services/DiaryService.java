package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.Fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.Fcms.FcmParams;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.DiaryNotice;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.HttpProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Component("diaryService")
@RequiredArgsConstructor
public class DiaryService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final UserLogRepository userLogRepository;
    private final DiaryNoticeRepository diaryNoticeRepository;
    private final TokenProvider tokenProvider;
    private final FcmProvider fcmProvider;
    private final HttpProvider httpProvider;
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
    public void setUserDiary(String token, long diaryID, String diaryName, int status){
        long userID = tokenProvider.getUserID(token);
        UserDiary userDiary = new UserDiary();
        userDiary.setUserID(userID);
        userDiary.setDiaryID(diaryID);
        userDiary.setDiaryName(diaryName);
        userDiary.setStatus(status);
        userDiaryRepository.save(userDiary);
    }

    @Transactional
    public void setDiaryNotice(String token, long diaryID, String notice){
        long userID = tokenProvider.getUserID(token);
        DiaryNotice diaryNotice = new DiaryNotice();
        diaryNotice.setUserID(userID);
        diaryNotice.setDiaryID(diaryID);
        diaryNotice.setNotice(notice);
        diaryNoticeRepository.save(diaryNotice);
    }

    @Transactional
    public void inviteDiary(String token, long diaryID, String userCode){
        UserData sendUserData = tokenProvider.decodeToken(token);
        long sendUserID = sendUserData.getUserID();
        UserInfoDetail receiveUserData = userRepository.getUserDataByUserCode(userCode);
        long receiveUserID = receiveUserData.getUserID();
        if(sendUserID == receiveUserID) throw new BusinessLogicException(BusinessLogicException.of.SELF_INVITE_EXCEPTION);

        Diary diary = diaryRepository.findByDiaryID(diaryID);
        if(diary == null) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_EXCEPTION);
        if(getDiaryStatus(diary.getStatus()) == 2) throw new BusinessLogicException(BusinessLogicException.of.ALONE_DIARY_INVITATION_EXCEPTION);
        String diaryName = diary.getDiaryName();

        UserDiary sendUserDiary = userDiaryRepository.findByUserIDAndDiaryID(sendUserID,diaryID);
        if(sendUserDiary == null) throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
        UserDiary receiveUserDiary = userDiaryRepository.findByUserIDAndDiaryID(receiveUserID,diaryID);
        if(receiveUserDiary != null){
            if(receiveUserDiary.getStatus()%10 == 0) throw new BusinessLogicException(BusinessLogicException.of.ALREADY_INVITE_EXCEPTION);
            else throw new BusinessLogicException(BusinessLogicException.of.EXIST_USER_DIARY_EXCEPTION);
        }

        setUserDiary(receiveUserID,diaryID,diaryName,(int) (receiveUserID*10));
        setUserLog(sendUserID,receiveUserID,diaryID,1,100);

        // "To. ".$receivename."님";
        String title = new StringBuilder()
                .append("To. ")
                .append(receiveUserData.getUserName())
                .append("님")
                .toString();

        // $sendname."님(".$usercode.")이 ".$diaryname."에 초대합니다:)";
        String body = new StringBuilder()
                .append(sendUserData.getUserName())
                .append("님(")
                .append(sendUserData.getUserCode())
                .append(")이 ")
                .append(diaryName)
                .append("에 초대합니다:)")
                .toString();

        FcmGroup fcmGroup = fcmProvider.getSingleUserFcmList(receiveUserID);
        FcmParams fcmParams = FcmParams.builder()
                .title(title)
                .body(body)
                .typeNum(1)
                .dataID(diaryID)
                .fcmGroup(fcmGroup)
                .build();
        httpProvider.getFcmKafkaProducer(receiveUserID, fcmParams);
    }

    @Transactional
    public void acceptDiary(String token, long diaryID, String userCode){
        UserData sendUserData = tokenProvider.decodeToken(token);
        long sendUserID = sendUserData.getUserID();
        UserInfoDetail receiveUserData = userRepository.getUserDataByUserCode(userCode);
        long receiveUserID = receiveUserData.getUserID();
        if(sendUserID == receiveUserID) throw new BusinessLogicException(BusinessLogicException.of.SELF_INVITE_EXCEPTION);

        Diary diary = diaryRepository.findByDiaryID(diaryID);
        if(diary == null) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_EXCEPTION);
        if(getDiaryStatus(diary.getStatus()) == 2) throw new BusinessLogicException(BusinessLogicException.of.ALONE_DIARY_INVITATION_EXCEPTION);

        setUserDiary(receiveUserID,diaryID,diary.getDiaryName(),diary.getStatus());
        setUserLog(receiveUserID,sendUserID,diaryID,1,999);
        setUserLog(sendUserID,receiveUserID,diaryID,2,100);
    }

    public int getDiaryStatus(int status, int color){
        if(status<1 || status>statusSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_STATUS_EXCEPTION);
        if(color<1 || color>colorSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return color*100 + status;
    }

    public boolean isSendRequest(String token, long diaryID){
//        if($data['id'] == $receiveID){
//            $res['isSuccess'] = FALSE;
//            $res['code'] = 501;
//            $res['message'] = '자기 자신을 등록할 수 없습니다.';
//            echo json_encode($res, JSON_NUMERIC_CHECK);
//            return;
//        }

        long userID = tokenProvider.getUserID(token);
        UserDiary userDiary = userDiaryRepository.findByUserIDAndDiaryID(userID,diaryID);
        return !(userDiary == null || userDiary.getStatus()%10 != 0);
    }

    @Transactional
    private void setUserDiary(long userID, long diaryID, String diaryName, int status){
        UserDiary userDiary = userDiaryRepository.findByUserIDAndDiaryID(userID,diaryID);
        if(userDiary == null){
            userDiary = new UserDiary();
            userDiary.setUserID(userID);
            userDiary.setDiaryID(diaryID);
            userDiary.setDiaryName(diaryName);
        }
        userDiary.setStatus(status);
        userDiaryRepository.save(userDiary);
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
