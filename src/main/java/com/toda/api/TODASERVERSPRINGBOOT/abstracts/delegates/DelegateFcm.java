package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseFcm;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Notification;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.enums.FcmTypes;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.JmsFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.NotificationRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserLogRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class DelegateFcm implements BaseFcm {
    private final PoolingHttpClientConnectionManager connectionManager;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final UserLogRepository userLogRepository;
    private final DelegateJms delegateJms;

    @Value("${toda.server.fcm.url}")
    private String fcmServerDomain;
    @Value("${toda.server.alarm.content-type}")
    private String contentType;
    @Value("${toda.server.alarm.authorization}")
    private String authorization;
    @Value("${toda.server.alarm.enable}")
    private boolean fcmEnable;

    /**
     * Fcm Jms Consumer
     * byte array 역직렬화 후 알림 발송
     * @param byteCode
     */
//    @JmsListener(destination = "fcm", containerFactory = "myFactory")
    @JmsListener(destination = "fcm")
    private void getFcmJmsConsumer(byte[] byteCode){
        try {
            JmsFcmProto.JmsFcmRequest jmsFcm = JmsFcmProto.JmsFcmRequest.parseFrom(byteCode);
            FcmGroup group = FcmGroup.builder().aosFcmList(jmsFcm.getAosFcmList()).iosFcmList(jmsFcm.getIosFcmList()).build();
            FcmParams params = FcmParams.builder().title(jmsFcm.getTitle()).body(jmsFcm.getBody()).typeNum(jmsFcm.getTypeNum()).dataID(jmsFcm.getDataID()).fcmGroup(group).build();

            List<String> aosFcmList = params.getFcmGroup().getAosFcmList();
            List<String> iosFcmList = params.getFcmGroup().getIosFcmList();
            if(!aosFcmList.isEmpty() || !iosFcmList.isEmpty()) sendFcm(params).get();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.MQ_CONNECTION_EXCEPTION);
        }
    }

    /**
     * AOS, IOS 별로 알림 발송
     * @param params
     * @return
     */
    private Future<Void> sendFcm(FcmParams params) {
        String fcmType = getFcmType(params.getTypeNum());
        List<String> aosFcmList = params.getFcmGroup().getAosFcmList();
        List<String> iosFcmList = params.getFcmGroup().getIosFcmList();

        if(!aosFcmList.isEmpty() || !iosFcmList.isEmpty()){
            try (CloseableHttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build()){
                CompletableFuture<FcmResponse> aosFcmFuture = !aosFcmList.isEmpty() ?
                        connectFcmUrl(getFcmAosBody(aosFcmList, params.getTitle(), params.getBody(), fcmType, params.getDataID()),client) : new CompletableFuture<>();
                CompletableFuture<FcmResponse> iosFcmFuture = !iosFcmList.isEmpty() ?
                        connectFcmUrl(getFcmIosBody(iosFcmList, params.getTitle(), params.getBody(), fcmType, params.getDataID()),client) : new CompletableFuture<>();
                CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(aosFcmFuture, iosFcmFuture);
                combinedFuture.join();
                return combinedFuture;
            }
            catch (IOException e){
                throw new WrongAccessException(WrongAccessException.of.HTTP_CLIENT_CREATE_EXCEPTION);
            }
        }
        return new CompletableFuture<>();
    }

    /**
     * FCM Server로 FCM 발송 API 전송
     * @param body
     * @param client
     * @return
     */
    private CompletableFuture<FcmResponse> connectFcmUrl(Object body, CloseableHttpClient client){
        // HttpClient 5.x 사용 버전
        return CompletableFuture.supplyAsync(()->{
            try(CloseableHttpResponse response = client.execute(getPostRequest(body))){
                return getFcmResponse(response);
            }
            catch (IOException e){
                throw new WrongAccessException(WrongAccessException.of.HTTP_CONNECTION_EXCEPTION);
            }
        }, taskExecutor);
    }

    /**
     * FCM API Response 받아 FcmResponse로 매핑
     * @param response
     * @return
     * @throws IOException
     */
    private FcmResponse getFcmResponse(CloseableHttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return new ObjectMapper().readValue(sb.toString(), FcmResponse.class);
    }

    /**
     * FCM 발송 데이터가 담긴 HttpPost 객체 생성
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    private HttpPost getPostRequest(Object body) throws JsonProcessingException {
        HttpPost postRequest = new HttpPost(fcmServerDomain);
        postRequest.setHeader("Authorization", authorization);
        postRequest.setHeader("Content-Type", contentType);
        postRequest.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body), ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
        return postRequest;
    }

    /**
     * 파라미터들을 FcmIosBody로 매핑
     * @param fcmList
     * @param title
     * @param body
     * @param type
     * @param dataID
     * @return
     */
    private FcmIos getFcmIosBody(List<String> fcmList, String title, String body, String type, long dataID){
        FcmIosNotification notification = FcmIosNotification.builder().title(title).body(body).type(type).build();
        FcmIosData data = FcmIosData.builder().data(dataID).build();
        return FcmIos.builder().registration_ids(fcmList).notification(notification).data(data).build();
    }

    /**
     * 파라미터들을 FcmAosBody로 매핑
     * @param fcmList
     * @param title
     * @param body
     * @param type
     * @param dataID
     * @return
     */
    private FcmAos getFcmAosBody(List<String> fcmList, String title, String body, String type, long dataID){
        FcmAosData data = FcmAosData.builder().title(title).body(body).type(type).data(dataID).build();
        return FcmAos.builder().registration_ids(fcmList).data(data).build();
    }

    /**
     * FcmType 생성
     * @param typeNum
     * @return
     */
    private String getFcmType(int typeNum){
        String key = new StringBuilder().append("TYPE_").append(typeNum).toString();
        return FcmTypes.valueOf(key).type;
    }

    @Override
    public void setJmsTopicFcm(long sendID, BiFunction<Long,String,Boolean> check, BiFunction<Long,String,FcmGroup> fcmGroup, FcmDto fcmDto) {
        if(fcmEnable){
            List<String> aosFcmList = new ArrayList<>();
            List<String> iosFcmList = new ArrayList<>();

            for(Map.Entry<Long,String> entry : fcmDto.getMap().entrySet()){
                long userID = entry.getKey();
                String userName = entry.getValue();
                System.out.println("pass 0");
                System.out.println(userID + " " + sendID);

                // 발신자와 수신자가 같을 경우 알림 미발송
                if(userID == sendID) continue;

                // 상대방 유저가 FCM 수신 조건 만족 시 발송
                if(check.apply(userID,userName)){
                    aosFcmList.addAll(fcmGroup.apply(userID,userName).getAosFcmList());
                    iosFcmList.addAll(fcmGroup.apply(userID,userName).getIosFcmList());
                }
            }

            try{
                JmsFcmProto.JmsFcmRequest request = JmsFcmProto.JmsFcmRequest.newBuilder()
                        .setTitle(fcmDto.getTitle())
                        .setBody(fcmDto.getBody())
                        .setTypeNum(fcmDto.getTypeNum())
                        .setDataID(fcmDto.getDataID())
                        .addAllAosFcm(aosFcmList)
                        .addAllIosFcm(iosFcmList)
                        .build();
                delegateJms.sendJmsMessage("fcm", request).get();
            }
            catch (InterruptedException | ExecutionException e){
                throw new WrongAccessException(WrongAccessException.of.SEND_FCM_EXCEPTION);
            }
        }
    }

    @Override
    public <T> Map<Long, String> getFcmReceiveUserMap(BiFunction<T, Map<Long, String>, Boolean> check, BiConsumer<T, Map<Long, String>> run, List<T> entityList) {
        Map<Long,String> receiveUserMap = new HashMap<>();
        for(T entity : entityList){
            if(check.apply(entity, receiveUserMap)) run.accept(entity, receiveUserMap);
        }
        return receiveUserMap;
    }

    @Override
    public void addUserLog(long sendUserID, long receiveUserID, long diaryID, int type, int status) {
        List<UserLog> userLogList = userLogRepository.findBySendIDAndReceiveIDAndTypeAndTypeID(sendUserID,receiveUserID,type,diaryID);
        if(userLogList.isEmpty()){
            UserLog userLog = new UserLog();
            userLog.setReceiveID(receiveUserID);
            userLog.setType(type);
            userLog.setTypeID(diaryID);
            userLog.setSendID(sendUserID);
            userLog.setStatus(status);
            userLogRepository.save(userLog);
        }
        else{
            UserLog curr = userLogList.get(0);
            curr.setStatus(100);
            curr.setCreateAt(LocalDateTime.now());
            userLogList.remove(0);

            userLogRepository.save(curr);
            userLogRepository.deleteAll(userLogList);
        }
    }

    @Override
    public String getFcmTitle() {
        return "투다에서 알림이 왔어요!";
    }

    @Override
    public String getFcmBody(String userName, String userCode, String objName, int type) {
        return switch (type) {
            case 1 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append("에 초대합니다:)").toString();
            case 2 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append("초대에 수락하셨습니다:)").toString();
            case 3 -> new StringBuilder().append(userName).append("님이 일기를 남겼습니다:)").toString();
            case 4 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 ").append(objName).append("님의 일기를 좋아합니다:)").toString();
            case 5 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 댓글을 남겼습니다:)").toString();
            case 6 -> new StringBuilder().append(userName).append("님(").append(userCode).append(")이 대댓글을 남겼습니다:)").toString();
            default -> throw new WrongAccessException(WrongAccessException.of.FCM_BODY_EXCEPTION);
        };
    }


    /**
     * 클라이언트 알림 발송 완료될때까지 임시로 사용
     * @param userID
     * @param notificationRepository
     * @return
     */
    public FcmGroup getUserFcmTokenList(long userID, NotificationRepository notificationRepository){
        List<String> aosFcmList = new ArrayList<>();
        List<String> iosFcmList = new ArrayList<>();

        Map<String,Integer> tokenStatus = getTokenStatus(userID, notificationRepository);
        for(String fcm : tokenStatus.keySet()){
            int status = tokenStatus.get(fcm);
            if(status == 100) iosFcmList.add(fcm);
            else if(status == 200) aosFcmList.add(fcm);
        }

        return FcmGroup.builder().aosFcmList(aosFcmList).iosFcmList(iosFcmList).build();
    }

    private Map<String, Integer> getTokenStatus(long userID, NotificationRepository notificationRepository) {
        return getFcmMap(userID,notificationRepository).getTokenStatus();
    }

    private FcmMap getFcmMap(long userID, NotificationRepository notificationRepository){
        List<Notification> userFcmList = notificationRepository.findByUserIDAndIsAllowedAndStatusNot(userID,"Y",0);

        System.out.println("pass 1");
        for(Notification notification : userFcmList) System.out.println(notification.getFcm());

        Map<String, Long> tokenIDs = userFcmList.stream()
                .collect(Collectors.toMap(
                        Notification::getFcm,
                        Notification::getNotificationID,
                        // Merge 함수를 사용하여 중복된 키에 대한 충돌 처리
                        (existingValue, newValue) -> existingValue
                ));
        Map<String, Integer> tokenStatus = userFcmList.stream()
                .collect(Collectors.toMap(
                        Notification::getFcm,
                        Notification::getStatus,
                        // Merge 함수를 사용하여 중복된 키에 대한 충돌 처리
                        (existingValue, newValue) -> existingValue
                ));
        return FcmMap.builder().tokenIDs(tokenIDs).tokenStatus(tokenStatus).build();
    }





    @PreDestroy
    public void shutdown() {
        taskExecutor.shutdown();
    }
}
