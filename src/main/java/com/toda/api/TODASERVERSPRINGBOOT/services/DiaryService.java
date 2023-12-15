package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.*;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryMemberListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.DiaryNoticeResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.providers.DiaryProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmTokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component("diaryService")
@RequiredArgsConstructor
public class DiaryService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final DiaryProvider diaryProvider;
    private final DiaryNoticeRepository diaryNoticeRepository;
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
        diaryProvider.updateList(deleteList, userDiary -> userDiary.setStatus(status),userDiaryRepository);

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
        Map<Long,String> receiveUserMap = new HashMap<>();
        for(UserDiary userDiary : acceptableDiaryList){
            long receiveUserID = userDiary.getStatus()/10;
            receiveUserMap.put(receiveUserID,userDiary.getUser().getUserName());
        }

        // 푸시 알림 발송 메시지 세팅
        String body = diaryProvider.getFcmBodyAccept(sendUserData.getUserName(), sendUserData.getUserCode(), diary.getDiaryName());      // $sendname."님(".$usercode.")이 ".$diaryname." 초대에 수락하셨습니다:)";
        for(Map.Entry<Long, String> entry : receiveUserMap.entrySet()){
            long userID = entry.getKey();
            String userName = entry.getValue();

            // 상대방 유저가 다이어리에 존재할 경우 FCM 메시지 발송 및 로그 체크
            int userDiaryStatus = getUserDiaryStatus(userID,diary.getDiaryID());
            if(userDiaryStatus == 100){
                diaryProvider.addUserLog(userID,sendUserData.getUserID(),diary.getDiaryID(),2,100);
                String title = diaryProvider.getFcmTitle(userName);                                              // "To. ".$receivename."님";
                FcmGroup fcmGroup = fcmTokenProvider.getSingleUserFcmList(userID);
                diaryProvider.sendFcm(userID, title, body, 2, diary.getDiaryID(), fcmGroup);
            }
        }
    }

    @Transactional
    public void deleteDiary(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        diaryProvider.updateList(userDiaryList,userDiary -> userDiary.setStatus(999),userDiaryRepository);
    }

    @Transactional
    public void rejectInvitation(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        diaryProvider.updateList(userDiaryList,userDiary -> userDiary.setStatus(999),userDiaryRepository);
        diaryProvider.updateUserLog(userID,diaryID,1,999);
    }

    @Transactional
    public void updateDiary(long userID, UpdateDiary updateDiary){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,updateDiary.getDiary(),999);
        int newStatus = diaryProvider.getDiaryStatus(updateDiary.getStatus(), updateDiary.getColor());

        diaryProvider.updateList(userDiaryList,userDiary -> {
            diaryProvider.checkDiaryStatus(userDiary,newStatus);
            userDiary.setDiaryName(updateDiary.getTitle());
            userDiary.setStatus(newStatus);
        },userDiaryRepository);
    }

    public DiaryRequestOfUser getRequestOfUser(long userID, long diaryID){
        List<DiaryRequestOfUser> requestList = userDiaryRepository.getDiaryRequestOfUser(diaryID,userID);
        return requestList.get(0);
    }

    public List<DiaryListResponse> getDiaryList(long userID, int status, int page){
        // 다이어리 정보 가져오기
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<DiaryList> diaryList = userDiaryRepository.getDiaryList(userID,status,pageable);

        // 가져온 정보 가공하여 정답 배열에 추가
        List<DiaryListResponse> res = new ArrayList<>();
        for(DiaryList curr : diaryList){
            int itemStatus = curr.getUserDiary().getStatus()%100;
            int itemColor = curr.getUserDiary().getStatus()/100;
            DiaryListResponse response = DiaryListResponse.builder()
                    .userName(curr.getUserDiary().getUser().getUserName())
                    .diaryID(curr.getUserDiary().getDiaryID())
                    .name(curr.getUserDiary().getDiaryName())
                    .color(diaryProvider.getDiaryColorCode(itemColor))
                    .colorCode(itemColor)
                    .status(itemStatus)
                    .userNum(curr.getUserNum())
                    .build();
            res.add(response);
        }
        return res;
    }

    public List<DiaryListResponse> getDiaryListWithKeyword(long userID, int status, int page, String keyword){
        // 다이어리 정보 가져오기
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<DiaryList> diaryList = userDiaryRepository.getDiaryListWithKeyword(userID,status,pageable,keyword);

        // 가져온 정보 가공하여 정답 배열에 추가
        List<DiaryListResponse> res = new ArrayList<>();
        for(DiaryList curr : diaryList){
            int itemStatus = curr.getUserDiary().getStatus()%100;
            int itemColor = curr.getUserDiary().getStatus()/100;
            DiaryListResponse response = DiaryListResponse.builder()
                    .userName(curr.getUserDiary().getUser().getUserName())
                    .diaryID(curr.getUserDiary().getDiaryID())
                    .name(curr.getUserDiary().getDiaryName())
                    .color(diaryProvider.getDiaryColorCode(itemColor))
                    .colorCode(itemColor)
                    .status(itemStatus)
                    .userNum(curr.getUserNum())
                    .build();
            res.add(response);
        }
        return res;
    }

    public List<DiaryMemberListResponse> getDiaryMemberList(long diaryID, int status, int page){
        // 다이어리 정보 가져오기
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<DiaryMemberList> diaryList = userDiaryRepository.getDiaryMemberList(diaryID,status,pageable);

        // 가져온 정보 가공하여 정답 배열에 추가
        List<DiaryMemberListResponse> res = new ArrayList<>();
        for(DiaryMemberList curr : diaryList){
            DiaryMemberListResponse response = DiaryMemberListResponse.builder()
                    .diaryID(curr.getUserDiary().getDiaryID())
                    .name(curr.getUserDiary().getDiaryName())
                    .userID(curr.getUserDiary().getUserID())
                    .userName(curr.getUserDiary().getUser().getUserName())
                    .userSelfie(curr.getSelfie())
                    .userNum(curr.getUserNum())
                    .build();
            res.add(response);
        }
        return res;
    }

    @Transactional
    public void updateNotice(long userID, long diaryID, String notice){
        List<DiaryNotice> noticeList = diaryNoticeRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,0);
        diaryProvider.updateList(noticeList,diaryNotice -> diaryNotice.setNotice(notice),diaryNoticeRepository);
    }

    @Transactional
    public DiaryNoticeResponse getDiaryNotice(long userID, long diaryID){
        String defaultNotice = "아직 등록된 공지가 없습니다 :D";
        List<DiaryNotice> diaryNoticeList = diaryNoticeRepository.findByDiaryIDAndStatusNotOrderByCreateAtDesc(diaryID,0);
        if(diaryNoticeList.isEmpty())
            return DiaryNoticeResponse.builder()
                    .diaryID(diaryID)
                    .diaryName("none")
                    .userID(userID)
                    .userName("none")
                    .notice("아직 등록된 공지가 없습니다 :D")
                    .date(0)
                    .build();

        DiaryNotice curr = diaryNoticeList.get(0);
        DiaryNoticeResponse res = DiaryNoticeResponse.builder()
                .diaryID(curr.getDiaryID())
                .diaryName(curr.getDiary().getDiaryName())
                .userID(curr.getUserID())
                .userName(curr.getUser().getUserName())
                .notice(curr.getNotice().equals("") ? defaultNotice : curr.getNotice())
                .date(diaryProvider.getTimeDiffSec(LocalDateTime.now(),curr.getCreateAt()))
                .build();

        // 1개 이상의 미사용 공지 삭제
        for(int i=1;i<diaryNoticeList.size();i++) diaryNoticeRepository.delete(diaryNoticeList.get(i));
        return res;
    }








    public int getUserDiaryStatus(long userID, long diaryID){
        return diaryProvider.getUserDiaryStatus(userID, diaryID);
    }

    public int getDiaryStatus(int status, int color){
        return diaryProvider.getDiaryStatus(status,color);
    }

    public List<UserDiary> getAcceptableDiaryList(long userID, long diaryID){
        return diaryProvider.getAcceptableDiaryList(userID, diaryID);
    }

    public UserData getSendUserData(String token){
        return diaryProvider.getSendUserData(token);
    }

    public UserInfoDetail getReceiveUserData(String userCode){
        return diaryProvider.getReceiveUserData(userCode);
    }

    public Diary getDiary(long diaryID){
        return diaryProvider.getDiary(diaryID);
    }

    public long getUserID(String token){
        return diaryProvider.getUserID(token);
    }
}
