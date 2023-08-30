package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.enums.FcmTypes;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.Fcms.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import lombok.RequiredArgsConstructor;
import org.apache.http.Consts;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class HttpProvider extends AbstractProvider implements BaseProvider {
    @Value("${toda.server.fcm.url}")
    private String fcmServerDomain;
    @Value("${toda.server.alarm.content-type}")
    private String contentType;
    @Value("${toda.server.alarm.authorization}")
    private String authorization;

    @KafkaListener(topics = "toda-fcm-topic")
    public void fcmKafkaConsumer(byte[] byteCode){
        logger.info("fcm pass");
        try {
            KafkaFcmProto.KafkaFcm kafkaFcm = KafkaFcmProto.KafkaFcm.parseFrom(byteCode);
            FcmGroup group = FcmGroup.builder()
                    .aosFcmList(kafkaFcm.getAosFcmListList())
                    .iosFcmList(kafkaFcm.getIosFcmListList())
                    .build();

            FcmParams params = FcmParams.builder()
                    .title(kafkaFcm.getTitle())
                    .body(kafkaFcm.getBody())
                    .typeNum(kafkaFcm.getTypeNum())
                    .dataID(kafkaFcm.getDataID())
                    .fcmGroup(group)
                    .build();

            sendFcmSingleUser(params).get();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.KAFKA_CONNECTION_EXCEPTION);
        }
    }

    private Future<Void> sendFcmSingleUser(FcmParams params) {
        String fcmType = getFcmType(params.getTypeNum());
        List<String> aosFcmList = params.getFcmGroup().getAosFcmList();
        List<String> iosFcmList = params.getFcmGroup().getIosFcmList();

        logger.info("fcm pass");

        if(!aosFcmList.isEmpty()){
            sendUrl(getFcmIosBody(
                    iosFcmList,
                    params.getTitle(),
                    params.getBody(),
                    fcmType,
                    params.getDataID()
            ));
        }

        if(!iosFcmList.isEmpty()){
            sendUrl(getFcmAosBody(
                    aosFcmList,
                    params.getTitle(),
                    params.getBody(),
                    fcmType,
                    params.getDataID()
            ));
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    private void sendUrl(Object body){
        ObjectMapper mapper = new ObjectMapper();
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost postRequest = new HttpPost(fcmServerDomain);
            postRequest.setHeader("Authorization", authorization);
            postRequest.setHeader("Content-Type", contentType);
            postRequest.setEntity(new StringEntity(mapper.writeValueAsString(body), Consts.UTF_8));
            client.execute(postRequest);
        } catch (Exception e) {
            throw new WrongAccessException(WrongAccessException.of.HTTP_CONNECTION_EXCEPTION);
        }
    }

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

    private String getFcmType(int typeNum){
        StringBuilder sb = new StringBuilder();
        String key = sb.append("TYPE_").append(typeNum).toString();
        return FcmTypes.valueOf(key).type;
    }
}
