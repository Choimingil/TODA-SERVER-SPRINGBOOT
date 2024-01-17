package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.*;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.DiaryListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.DiaryMemberListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.DiaryNoticeResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.InviteRequestResponse;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component("diaryService")
public class DiaryService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserLogRepository userLogRepository;
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final DiaryNoticeRepository diaryNoticeRepository;

    public DiaryService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateJms delegateJms,
            UserRepository userRepository,
            UserLogRepository userLogRepository,
            DiaryRepository diaryRepository,
            UserDiaryRepository userDiaryRepository,
            DiaryNoticeRepository diaryNoticeRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateJms);
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
        this.diaryRepository = diaryRepository;
        this.userDiaryRepository = userDiaryRepository;
        this.diaryNoticeRepository = diaryNoticeRepository;
    }

    @Transactional
    public long addDiary(String diaryName, int status){
        Diary diary = new Diary();
        diary.setDiaryName(diaryName);
        diary.setStatus(status);
        Diary newDiary = diaryRepository.save(diary);
        return newDiary.getDiaryID();
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
    public void inviteDiary(UserData sendUserData, UserInfoDetail receiveUserData, Diary diary){
        long sendUserID = sendUserData.getUserID();
        long receiveUserID = receiveUserData.getUserID();
        long diaryID = diary.getDiaryID();
        String diaryName = diary.getDiaryName();
        int status = (int)(sendUserID*10);

        // 이미 다이어리를 탈퇴한 기록이 있는 경우 해당 값을 초대값으로 변경
        List<UserDiary> deleteList = userDiaryRepository.findByUserIDAndDiaryIDAndStatus(receiveUserID,diaryID,999);
        AtomicBoolean isEdit = new AtomicBoolean(false);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(status);
                    isEdit.set(true);
                },
                deleteList,
                userDiaryRepository
        );

        // 다이어리 탈퇴 기록이 없다면 새롭게 추가 진행
        if(!isEdit.get()) addUserDiary(receiveUserID,diaryID,diaryName,status);
    }

    @Transactional
    public void acceptDiary(List<UserDiary> acceptableDiaryList, long receiveUserID, int status){
        List<UserDiary> copy = new ArrayList<>();
        Collections.copy(acceptableDiaryList, copy);

        updateListAndDelete(
                userDiary -> {
                    int originStatus = userDiary.getStatus();
                    return originStatus/10 == receiveUserID;
                },
                userDiary -> {
                    userDiary.setStatus(status);

                    // 초대 완료 로그 추가
                    List<UserLog> userLogList = userLogRepository.findByReceiveIDAndTypeAndTypeIDAndStatusNot(receiveUserID,1,userDiary.getDiaryID(),999);
                    updateList(userLogList,userLog -> userLog.setStatus(status),userLogRepository);
                },
                copy, userDiaryRepository
        );
    }

    @Transactional
    public void setFcmAndLog(Map<Long,String> receiveUserMap, UserData sendUserData, Diary diary, int type){

        setJmsTopicFcm(
                sendUserData.getUserID(),
                (userID, userName) -> {
                    // 초대 시 발송 조건 : 상대방 유저가 다이어리 초대를 받았을 경우
                    if(type == 1) return getUserDiaryStatus(userID,diary.getDiaryID()) == 200;
                        // 승낙 시 발송 조건 : 상대방 유저가 다이어리에 존재할 경우
                    else if(type == 2) return getUserDiaryStatus(userID,diary.getDiaryID()) == 100;
                    else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
                },
                // 조건 만족 시 FCM 발송
                (userID, userName) -> {
                    addUserLog(userID,sendUserData.getUserID(),diary.getDiaryID(),type,100);
                    return getUserFcmTokenList(userID);
                },
                FcmDto.builder()
                        .title(getFcmTitle())
                        .body(getFcmBody(sendUserData.getUserName(), sendUserData.getUserCode(), diary.getDiaryName(), type))
                        .typeNum(type)
                        .dataID(diary.getDiaryID())
                        .map(receiveUserMap)
                        .build()
        );
    }

    @Transactional
    public void deleteDiary(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        AtomicBoolean isEdit = new AtomicBoolean(false);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(999);
                    isEdit.set(true);
                },
                userDiaryList,
                userDiaryRepository
        );
    }

    @Transactional
    public void rejectInvitation(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        AtomicBoolean isEdit = new AtomicBoolean(false);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(999);
                    isEdit.set(true);
                },
                userDiaryList,
                userDiaryRepository
        );

        List<UserLog> userLogList = userLogRepository.findByReceiveIDAndTypeAndTypeIDAndStatusNot(userID,1,diaryID,999);
        updateList(userLogList,userLog -> userLog.setStatus(999),userLogRepository);
    }

    @Transactional
    public void updateDiary(long userID, UpdateDiary updateDiary){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,updateDiary.getDiary(),999);
        int newStatus = getDiaryStatus(updateDiary.getStatus(), updateDiary.getColor());
        AtomicBoolean isEdit = new AtomicBoolean(false);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    checkDiaryStatus(userDiary,newStatus);
                    userDiary.setDiaryName(updateDiary.getTitle());
                    userDiary.setStatus(newStatus);
                },
                userDiaryList,
                userDiaryRepository
        );
    }

    public List<InviteRequestResponse> getInviteRequest(long userID, long diaryID){
        List<InviteRequest> requestList = userDiaryRepository.getInviteRequest(userID,diaryID);
        List<InviteRequestResponse> responseList = new ArrayList<>();
        for(InviteRequest request : requestList){
            responseList.add(InviteRequestResponse.builder()
                    .userID(request.getUserDiary().getUserID())
                    .userCode(request.getUserDiary().getUser().getUserCode())
                    .email(request.getUserDiary().getUser().getEmail())
                    .name(request.getUserDiary().getUser().getUserName())
                    .selfie(request.getSelfie())
                    .diaryID(request.getUserDiary().getDiaryID())
                    .diaryName(request.getUserDiary().getDiary().getDiaryName())
                    .date(getDateString(request.getUserDiary().getCreateAt()))
                    .build()
            );
        }
        return responseList;
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
                    .color(getDiaryColorCode(colorSet,itemColor))
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
                    .color(getDiaryColorCode(colorSet,itemColor))
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
        AtomicBoolean isEdit = new AtomicBoolean(false);
        updateListAndDelete(
                diaryNotice -> isEdit.get(),
                diaryNotice -> {
                    diaryNotice.setNotice(notice);
                    isEdit.set(true);
                },
                noticeList,
                diaryNoticeRepository
        );
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
                .date(getTimeDiffSec(LocalDateTime.now(),curr.getCreateAt()))
                .build();

        // 1개 이상의 미사용 공지 삭제
        for(int i=1;i<diaryNoticeList.size();i++) diaryNoticeRepository.delete(diaryNoticeList.get(i));
        return res;
    }





    /**
     * 다이어리 상태값 유효성 검증
     * 1 : 함께 쓰는 다이어리
     * 2 : 혼자 쓰는 다이어리
     * 3 : 즐겨 찾는 다이어리
     * @param origin
     * @param newStatus
     */
    private void checkDiaryStatus(UserDiary origin, int newStatus){
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

    private int getDiaryColorCode(Set<DiaryColors> colorSet, int color){
        StringBuilder sb = new StringBuilder();
        sb.append("CODE_").append(color);
        String key = sb.toString();
        if(!colorSet.contains(DiaryColors.valueOf(key)))
            throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return Integer.parseInt(DiaryColors.valueOf(key).code);
    }

    /**
     * 다이어리 초대 요청 시 FCM 발송받을 유저 데이터 getter
     * @param receiveUserDataList
     * @return
     */
    public Map<Long,String> getFcmDiaryInviteUserMap(List<UserInfoDetail> receiveUserDataList){
        return getFcmReceiveUserMap(
                (userInfoDetail,map)-> !map.containsKey(userInfoDetail.getUserID()),
                (userInfoDetail,map)-> map.put(
                        userInfoDetail.getUserID(),
                        userInfoDetail.getUserName()
                ),
                receiveUserDataList
        );
    }

    /**
     * 다이어리 초대 승낙 시 FCM 발송받을 유저 데이터 getter
     * @param entityList
     * @return
     */
    public Map<Long,String> getFcmDiaryAcceptUserMap(List<UserDiary> entityList){
        return getFcmReceiveUserMap(
                (userDiary,map)-> !map.containsKey((long)userDiary.getStatus()/10),
                (userDiary,map)-> map.put(
                        (long)userDiary.getStatus()/10,
                        userDiary.getUser().getUserName()
                ),
                entityList
        );
    }

    public int getDiaryStatus(int status, int color){
        return getStatus(color,status,100,() -> {
            if(status<1 || status>statusSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_STATUS_EXCEPTION);
            if(color<1 || color>colorSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        });
    }

    public List<UserDiary> getAcceptableDiaryList(long userID, long diaryID){
        List<UserDiary> res = new ArrayList<>();
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryID(userID,diaryID);
        for(UserDiary userDiary : userDiaryList) if((userDiary.getStatus()%10) == 0) res.add(userDiary);
        return res;
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
}
