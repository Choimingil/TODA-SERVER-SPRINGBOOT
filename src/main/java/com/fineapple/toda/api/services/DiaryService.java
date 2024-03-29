package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
import com.fineapple.toda.api.entities.Diary;
import com.fineapple.toda.api.entities.UserDiary;
import com.fineapple.toda.api.entities.UserLog;
import com.fineapple.toda.api.entities.mappings.DiaryList;
import com.fineapple.toda.api.entities.mappings.DiaryMemberList;
import com.fineapple.toda.api.entities.mappings.InviteRequest;
import com.fineapple.toda.api.exceptions.BusinessLogicException;
import com.fineapple.toda.api.exceptions.WrongArgException;
import com.fineapple.toda.api.models.bodies.UpdateDiary;
import com.fineapple.toda.api.models.dtos.FcmDto;
import com.fineapple.toda.api.models.responses.get.DiaryListResponse;
import com.fineapple.toda.api.models.responses.get.DiaryMemberListResponse;
import com.fineapple.toda.api.models.responses.get.DiaryNoticeResponse;
import com.fineapple.toda.api.models.responses.get.InviteRequestResponse;
import com.fineapple.toda.api.repositories.*;
import com.fineapple.toda.api.entities.DiaryNotice;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import com.fineapple.toda.api.enums.DiaryColors;
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
    private final NotificationRepository notificationRepository;

    public DiaryService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            UserRepository userRepository,
            UserLogRepository userLogRepository,
            DiaryRepository diaryRepository,
            UserDiaryRepository userDiaryRepository,
            DiaryNoticeRepository diaryNoticeRepository,
            NotificationRepository notificationRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
        this.diaryRepository = diaryRepository;
        this.userDiaryRepository = userDiaryRepository;
        this.diaryNoticeRepository = diaryNoticeRepository;
        this.notificationRepository = notificationRepository;
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
    public void inviteDiary(UserDetail sendUser, UserDetail receiveUserDetail, Diary diary){
        long sendUserID = sendUser.getUser().getUserID();
        long receiveUserID = receiveUserDetail.getUser().getUserID();
        long diaryID = diary.getDiaryID();
        String diaryName = diary.getDiaryName();
        int status = (int)(sendUserID*10);

        // 이미 다이어리를 탈퇴한 기록이 있는 경우 해당 값을 초대값으로 변경
        List<UserDiary> deleteList = userDiaryRepository.findByUserIDAndDiaryIDAndStatus(receiveUserID,diaryID,999);
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(status);
                    isEdit.set(false);
                },
                deleteList,
                userDiaryRepository
        );

        // 다이어리 탈퇴 기록이 없다면 새롭게 추가 진행
        if(isEdit.get()) addUserDiary(receiveUserID,diaryID,diaryName,status);

        // 유저 로그 추가
        addUserLog(sendUserID,receiveUserID,diary.getDiaryID(),1,100);
    }

    @Transactional
    public void acceptDiary(long sendUserID, long receiveUserID, long diaryID, int status, List<UserDiary> userDiaryList){
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(status);

                    // 초대 완료 로그 추가
                    List<UserLog> userLogList = userLogRepository.findByReceiveIDAndTypeAndTypeIDAndStatusNot(receiveUserID,1,userDiary.getDiaryID(),999);
                    updateList(userLogList,userLog -> userLog.setStatus(status),userLogRepository);

                    isEdit.set(false);
                },
                userDiaryList,
                userDiaryRepository
        );

        // 유저 로그 추가
        addUserLog(sendUserID,receiveUserID,diaryID,2,100);
    }

    @Transactional
    public void setFcmAndLog(Map<Long,String> receiveUserMap, UserDetail sendUser, Diary diary, int type){
        setJmsTopicFcm(
                sendUser.getUser().getUserID(),
                (userID, userName) -> {
                    // 초대 시 발송 조건 : 상대방 유저가 다이어리 초대를 받았을 경우
                    if(type == 1) return getUserDiaryStatus(userID,diary.getDiaryID()) == 200;
                    // 승낙 시 발송 조건 : 상대방 유저가 다이어리에 존재할 경우
                    else if(type == 2) return getUserDiaryStatus(userID,diary.getDiaryID()) == 100;
                    else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
                },
                // 조건 만족 시 FCM 발송
                (userID, userName) -> getUserFcmTokenList(userID, notificationRepository),
                FcmDto.builder()
                        .title(getFcmTitle())
                        .body(getFcmBody(sendUser.getUser().getUserName(), sendUser.getUser().getUserCode(), diary.getDiaryName(), type))
                        .typeNum(type)
                        .dataID(diary.getDiaryID())
                        .map(receiveUserMap)
                        .build()
        );
    }

    @Transactional
    public void deleteDiary(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(999);
                    isEdit.set(false);
                },
                userDiaryList,
                userDiaryRepository
        );
    }

    @Transactional
    public void rejectInvitation(long userID, long diaryID){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    userDiary.setStatus(999);
                    isEdit.set(false);
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

        Diary currDiary = diaryRepository.findByDiaryID(updateDiary.getDiary());
        int status = updateDiary.getStatus() != 3 ? updateDiary.getStatus() : (currDiary.getStatus()%100 == 1 ? 3 : 4);
        int newStatus = getDiaryStatus(status, updateDiary.getColor());

        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                userDiary -> isEdit.get(),
                userDiary -> {
                    checkDiaryStatus(userDiary,newStatus);
                    userDiary.setDiaryName(updateDiary.getTitle());
                    userDiary.setStatus(newStatus);
                    isEdit.set(false);
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
                    .userID(request.getUser().getUserID())
                    .userCode(request.getUser().getUserCode())
                    .email(request.getUser().getEmail())
                    .name(request.getUser().getUserName())
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
        List<DiaryList> diaryList = userDiaryRepository.getDiaryList(userID,getStatusList(status),pageable);

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
        List<DiaryList> diaryList = userDiaryRepository.getDiaryListWithKeyword(userID,getStatusList(status),pageable,keyword);

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
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                diaryNotice -> isEdit.get(),
                diaryNotice -> {
                    diaryNotice.setNotice(notice);
                    isEdit.set(false);
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
            if(newDiaryStatus != origin.getDiary().getStatus()%100)
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
     * @param receiveUserDetailList
     * @return
     */
    public Map<Long,String> getFcmDiaryInviteUserMap(List<UserDetail> receiveUserDetailList){
        return getFcmReceiveUserMap(
                (userDetail,map)-> !map.containsKey(userDetail.getUser().getUserID()),
                (userDetail,map)-> map.put(
                        userDetail.getUser().getUserID(),
                        userDetail.getUser().getUserName()
                ),
                receiveUserDetailList
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

    public List<UserDiary> getAcceptableDiaryList(long sendUserID, long receiveUserID, long diaryID){
        return userDiaryRepository.getAcceptableRequest(sendUserID,receiveUserID,diaryID);
    }

    public UserDetail getReceiveUserDetail(String userCode){
        return userRepository.getUserDetailByUserCode(userCode);
    }

    public Diary getDiary(long diaryID){
        Diary diary = diaryRepository.findByDiaryID(diaryID);
        if(diary == null) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_EXCEPTION);
        if(diary.getStatus()%100 == 2) throw new BusinessLogicException(BusinessLogicException.of.ALONE_DIARY_INVITATION_EXCEPTION);
        return diary;
    }

    private List<Integer> getStatusList(int status){
        List<Integer> statusList = new ArrayList<>();
        statusList.add(status);

        if(status == 1) statusList.add(3);
        else if(status == 2) statusList.add(4);
        else if(status == 3) statusList.add(4);
        else throw new WrongArgException(WrongArgException.of.WRONG_DIARY_STATUS_EXCEPTION);

        return statusList;
    }
}
