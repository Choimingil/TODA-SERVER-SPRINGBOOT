package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.google.protobuf.MessageLite;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractService extends AbstractUtil implements BaseService {
    protected final Logger logger = LoggerFactory.getLogger(AbstractService.class);
    protected final Set<Long> basicStickers = Set.of(1L,2L,3L,4L);
    protected final Set<DiaryColors> colorSet = EnumSet.allOf(DiaryColors.class);
    protected final Set<DiaryStatus> statusSet = EnumSet.allOf(DiaryStatus.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;
    private final DelegateFcm delegateFcm;
    private final DelegateUserAuth delegateUserAuth;
    private final DelegateFcmTokenAuth delegateFcmTokenAuth;
    private final DelegateJms delegateJms;

    public AbstractService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateJms delegateJms
    ) {
        super(delegateDateTime, delegateFile, delegateStatus);
        this.delegateJwt = delegateJwt;
        this.delegateFcm = delegateFcm;
        this.delegateUserAuth = delegateUserAuth;
        this.delegateFcmTokenAuth = delegateFcmTokenAuth;
        this.delegateJms = delegateJms;
    }

    protected long getUserID(String token) {
        return delegateJwt.getUserID(token);
    }
    protected String createToken(Authentication authentication, UserData userData) {
        return delegateJwt.createToken(authentication,userData);
    }
    protected UserData decodeToken(String token) {
        return delegateJwt.decodeToken(token);
    }
    protected UserData getUserInfo(String value) {
        return delegateUserAuth.getUserInfo(value);
    }
    protected void setUserInfo(UserData userData) {
        delegateUserAuth.setUserInfo(userData);
    }
    protected void deleteUserInfo(String email) {
        delegateUserAuth.deleteUserInfo(email);
    }
    protected FcmGroup getUserFcmTokenList(long userID) {
        return delegateFcmTokenAuth.getUserFcmTokenList(userID);
    }
    protected long getNotificationID(long userID, String fcm) {
        return delegateFcmTokenAuth.getNotificationID(userID,fcm);
    }
    protected void setNewFcm(long userID, String fcm, long notificationID, int status) {
        delegateFcmTokenAuth.setNewFcm(userID,fcm,notificationID,status);
    }
    protected void deleteFcm(long userID, String fcm) {
        delegateFcmTokenAuth.deleteFcm(userID,fcm);
    }
    protected void setJmsTopicFcm(long sendID, BiFunction<Long,String,Boolean> check, BiFunction<Long,String, FcmGroup> fcmGroup, FcmDto fcmDto) {
        delegateFcm.setJmsTopicFcm(sendID,check,fcmGroup,fcmDto);
    }
    protected <T> Map<Long, String> getFcmReceiveUserMap(BiFunction<T, Map<Long, String>, Boolean> check, BiConsumer<T, Map<Long, String>> run, List<T> entityList) {
        return delegateFcm.getFcmReceiveUserMap(check,run,entityList);
    }
    protected void addUserLog(long sendUserID, long receiveUserID, long diaryID, int type, int status) {
        delegateFcm.addUserLog(sendUserID,receiveUserID,diaryID,type,status);
    }
    protected String getFcmTitle() {
        return delegateFcm.getFcmTitle();
    }
    protected String getFcmBody(String userName, String userCode, String objName, int type) {
        return delegateFcm.getFcmBody(userName,userCode,objName,type);
    }
    protected CompletableFuture<Boolean> sendJmsMessage(String destination, MessageLite message) {
        return delegateJms.sendJmsMessage(destination,message);
    }

    @Override
    public <T> void updateListAndDelete(Function<T,Boolean> check, Consumer<T> run, List<T> entityList, JpaRepository<T, Long> repository) {
        if(!entityList.isEmpty()) {
            List<T> saveList = new ArrayList<>();
            List<T> deleteList = new ArrayList<>();
            for(T entity : entityList){
                if(check.apply(entity)){
                    run.accept(entity);
                    saveList.add(entity);
                }
                else deleteList.add(entity);
            }
            if(!saveList.isEmpty()) repository.saveAll(saveList);
            if(!deleteList.isEmpty()) repository.deleteAll(deleteList);
        }
    }

    @Override
    public <T> void updateList(List<T> entityList, Consumer<T> run, JpaRepository<T, Long> repository){
        if(!entityList.isEmpty()){
            List<T> res = new ArrayList<>();
            for(T entity : entityList){
                run.accept(entity);
                res.add(entity);
            }
            repository.saveAll(res);
        }
    }


}
