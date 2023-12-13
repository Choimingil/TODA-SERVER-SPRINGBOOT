package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.enums.FcmTypes;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;



import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.springframework.web.client.RestTemplate;


//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.Consts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class FcmProvider extends AbstractProvider implements BaseProvider {
    private final FcmTokenProvider fcmTokenProvider;

    @Value("${toda.server.fcm.url}")
    private String fcmServerDomain;
    @Value("${toda.server.alarm.content-type}")
    private String contentType;
    @Value("${toda.server.alarm.authorization}")
    private String authorization;

    /**
     * Fcm Kafka Consumer
     * byte array 역직렬화 후 알림 발송
     * @param byteCode
     */
    @KafkaListener(topics = "fcm")
    private void getFcmKafkaConsumer(byte[] byteCode){
        try {
            KafkaFcmProto.KafkaFcmRequest kafkaFcm = KafkaFcmProto.KafkaFcmRequest.parseFrom(byteCode);
            long userID = kafkaFcm.getUserID();
            FcmGroup group = FcmGroup.builder()
                    .aosFcmList(kafkaFcm.getAosFcmList())
                    .iosFcmList(kafkaFcm.getIosFcmList())
                    .build();
            FcmParams params = FcmParams.builder()
                    .title(kafkaFcm.getTitle())
                    .body(kafkaFcm.getBody())
                    .typeNum(kafkaFcm.getTypeNum())
                    .dataID(kafkaFcm.getDataID())
                    .fcmGroup(group)
                    .build();

            sendFcmSingleUser(userID, params).get();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.KAFKA_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 하나의 유저에게 알림 발송
     * @param userID
     * @param params
     * @return
     */
    private Future<Void> sendFcmSingleUser(long userID, FcmParams params) {
        String fcmType = getFcmType(params.getTypeNum());
        List<String> aosFcmList = params.getFcmGroup().getAosFcmList();
        List<String> iosFcmList = params.getFcmGroup().getIosFcmList();

        FcmResponse aosResponse =
                !aosFcmList.isEmpty() ?
                        sendUrl(getFcmAosBody(aosFcmList, params.getTitle(), params.getBody(), fcmType, params.getDataID())) :
                        null;

        FcmResponse iosResponse =
                !iosFcmList.isEmpty() ?
                        sendUrl(getFcmIosBody(iosFcmList, params.getTitle(), params.getBody(), fcmType, params.getDataID())) :
                        null;

        if(aosResponse != null) checkExpiredTokens(aosResponse, userID, aosFcmList);
        if(iosResponse != null) checkExpiredTokens(iosResponse, userID, iosFcmList);

        return CompletableFuture.completedFuture(null);
    }

    /**
     * 만료된 토큰이 있을 경우 DB 및 Redis에서 삭제
     * @param response
     * @param userID
     * @param tokenList
     */
    private void checkExpiredTokens(FcmResponse response, long userID, List<String> tokenList){
        List<FcmResponse.Result> results = response.getResults();
        for(int i=0;i<results.size();i++){
            if(results.get(i).getError() != null && results.get(i).getError().equals("NotRegistered"))
                fcmTokenProvider.deleteFcm(userID,tokenList.get(i));
        }
    }

    /**
     * FCM Server로 FCM 발송 API 전송
     * @param body
     * @return
     */
    @Async
    private FcmResponse sendUrl(Object body){
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try{
//            HttpPost postRequest = new HttpPost(fcmServerDomain);
//            postRequest.setHeader("Authorization", authorization);
//            postRequest.setHeader("Content-Type", contentType);
//            postRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(body), Consts.UTF_8));
//            ResponseEntity<String> responseEntity = restTemplate.postForEntity(fcmServerDomain, postRequest, String.class);
//
//            HttpStatusCode statusCode = responseEntity.getStatusCode();
//            String responseBody = responseEntity.getBody();
//
//            logger.info("HTTP status code : "+statusCode);
//            logger.info("HTTP response body : "+responseBody);
//
//            return new ObjectMapper().readValue(responseBody, FcmResponse.class);
//        }
//        catch (JsonProcessingException e){
//            throw new WrongAccessException(WrongAccessException.of.HTTP_CONNECTION_EXCEPTION);
//        }





        // HttpClient 5.x 사용 버전
        ObjectMapper mapper = new ObjectMapper();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);

        try (CloseableHttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build()) {
            HttpPost postRequest = new HttpPost(fcmServerDomain);
            postRequest.setHeader("Authorization", authorization);
            postRequest.setHeader("Content-Type", contentType);
            postRequest.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));

            try(CloseableHttpResponse response = client.execute(postRequest)){
                return getFcmResponse(response);
            }
        }
        catch (IOException e){
            throw new WrongAccessException(WrongAccessException.of.HTTP_CONNECTION_EXCEPTION);
        }



//        // HttpClient 4.x 사용 버전
//        ObjectMapper mapper = new ObjectMapper();
//        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
//            HttpPost postRequest = new HttpPost(fcmServerDomain);
//            postRequest.setHeader("Authorization", authorization);
//            postRequest.setHeader("Content-Type", contentType);
//            postRequest.setEntity(new StringEntity(mapper.writeValueAsString(body), Consts.UTF_8));
//
//            try(CloseableHttpResponse response = client.execute(postRequest)){
//                return getFcmResponse(response);
//            }
//        } catch (Exception e) {
//            throw new WrongAccessException(WrongAccessException.of.HTTP_CONNECTION_EXCEPTION);
//        }
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
     * 파라미터들을 FcmIosBody로 매핑
     * @param fcmList
     * @param title
     * @param body
     * @param type
     * @param dataID
     * @return
     */
    private FcmIos getFcmIosBody(List<String> fcmList, String title, String body, String type, long dataID){
        FcmIosNotification notification = FcmIosNotification.builder()
                .title(title)
                .body(body)
                .type(type)
                .build();
        FcmIosData data = FcmIosData.builder().data(dataID).build();
        return FcmIos.builder()
                .registration_ids(fcmList)
                .notification(notification)
                .data(data)
                .build();
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
        FcmAosData data = FcmAosData.builder()
                .title(title)
                .body(body)
                .type(type)
                .data(dataID)
                .build();
        return FcmAos.builder()
                .registration_ids(fcmList)
                .data(data)
                .build();
    }

    /**
     * FcmType 생성
     * @param typeNum
     * @return
     */
    private String getFcmType(int typeNum){
        StringBuilder sb = new StringBuilder();
        String key = sb.append("TYPE_").append(typeNum).toString();
        return FcmTypes.valueOf(key).type;
    }
}
