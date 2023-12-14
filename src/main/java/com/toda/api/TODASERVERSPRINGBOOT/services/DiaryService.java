package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.DiaryRequestOfUser;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.providers.DiaryProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmTokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component("diaryService")
@RequiredArgsConstructor
public class DiaryService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final DiaryProvider diaryProvider;
    private final TokenProvider tokenProvider;
    private final FcmTokenProvider fcmTokenProvider;

    @Transactional
    public long addDiary(String diaryName, int status){
        Diary diary = new Diary();
        diary.setDiaryName(diaryName);
        diary.setStatus(status);
        Diary newDiary = diaryRepository.save(diary);
        return newDiary.getDiaryID();
    }

    @Transactional
    public void setDiaryInfo(long userID, long diaryID, String diaryName, int status){
        diaryProvider.addUserDiary(userID, diaryID, diaryName, status);
        diaryProvider.addDiaryNotice(userID, diaryID, "");
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
        diaryProvider.updateUserDiaryList(deleteList, userDiary -> userDiary.setStatus(status));

        // 로그 추가
        diaryProvider.addUserLog(sendUserID,receiveUserID,diaryID,1,100);

        // 초대 FCM 전송
        String title = diaryProvider.getFcmTitle(receiveUserData.getUserName());                                              // "To. ".$receivename."님";
        String body = diaryProvider.getFcmBodyInvite(sendUserData.getUserName(), sendUserData.getUserCode(), diaryName);      // $sendname."님(".$usercode.")이 ".$diaryname."에 초대합니다:)";
        FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(receiveUserID);
        diaryProvider.sendFcm(receiveUserID, title, body, 1, diaryID, fcmGroup);
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
                    diaryProvider.updateUserLog(receiveUserID,userDiary.getDiaryID(),1,999);

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
        String body = diaryProvider.getFcmBodyAccept(sendUserData.getUserName(), sendUserData.getUserCode(), diary.getDiaryName());      // $sendname."님(".$usercode.")이 ".$diaryname." 초대에 수락하셨습니다:)";
        for(User receiveUser : receiveUserData){
            // 상대방 유저가 다이어리에 존재할 경우 FCM 메시지 발송 및 로그 체크
            int userDiaryStatus = getUserDiaryStatus(receiveUser.getUserID(),diary.getDiaryID());
            if(userDiaryStatus == 100){
                diaryProvider.addUserLog(receiveUser.getUserID(),sendUserData.getUserID(),diary.getDiaryID(),2,100);
                String title = diaryProvider.getFcmTitle(receiveUser.getUserName());                                              // "To. ".$receivename."님";
                FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(receiveUser.getUserID());
                diaryProvider.sendFcm(receiveUser.getUserID(), title, body, 2, diary.getDiaryID(), fcmGroup);
            }
        }
    }

    @Transactional
    public void deleteDiary(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        diaryProvider.updateUserDiaryList(userDiaryList,userDiary -> userDiary.setStatus(999));
    }

    @Transactional
    public void rejectInvitation(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        diaryProvider.updateUserDiaryList(userDiaryList,userDiary -> userDiary.setStatus(999));
        diaryProvider.updateUserLog(userID,diaryID,1,999);
    }

    @Transactional
    public void updateDiary(long userID, UpdateDiary updateDiary){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,updateDiary.getDiary(),999);
        int newStatus = diaryProvider.getDiaryStatus(updateDiary.getStatus(), updateDiary.getColor());

        diaryProvider.updateUserDiaryList(userDiaryList,userDiary -> {
            diaryProvider.checkDiaryStatus(userDiary,newStatus);
            userDiary.setDiaryName(updateDiary.getTitle());
            userDiary.setStatus(newStatus);
        });
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
        return diaryProvider.getDiaryStatus(status,color);
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
}
