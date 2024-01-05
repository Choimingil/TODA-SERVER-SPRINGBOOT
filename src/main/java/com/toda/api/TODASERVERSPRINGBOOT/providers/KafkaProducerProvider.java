package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaProducerProvider extends AbstractProvider implements BaseProvider {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    /**
     * Fcm Kafka Producer
     * KafkaFcmProto 프로토콜 버퍼를 byte array로 만들어 kafka로 send
     * @param message
     */
    @Async
    public CompletableFuture<Boolean> getKafkaProducer(String topic, com.google.protobuf.MessageLite message){
        kafkaTemplate.send(topic, message.toByteArray());
        return CompletableFuture.completedFuture(true);
    }

//    /**
//     * Fcm Kafka Consumer
//     * byte array 역직렬화 후 알림 발송
//     * @param byteCode
//     */
//    @KafkaListener(topics = "fcm")
//    private void getFcmKafkaConsumer(byte[] byteCode){
//        try {
//            KafkaFcmProto.KafkaFcmRequest kafkaFcm = KafkaFcmProto.KafkaFcmRequest.parseFrom(byteCode);
//            long userID = kafkaFcm.getUserID();
//            FcmGroup group = FcmGroup.builder()
//                    .aosFcmList(kafkaFcm.getAosFcmList())
//                    .iosFcmList(kafkaFcm.getIosFcmList())
//                    .build();
//            FcmParams params = FcmParams.builder()
//                    .title(kafkaFcm.getTitle())
//                    .body(kafkaFcm.getBody())
//                    .typeNum(kafkaFcm.getTypeNum())
//                    .dataID(kafkaFcm.getDataID())
//                    .fcmGroup(group)
//                    .build();
//
//            fcmProvider.sendFcmSingleUser(userID, params).get();
//        }
//        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
//            throw new WrongAccessException(WrongAccessException.of.KAFKA_CONNECTION_EXCEPTION);
//        }
//    }

//    /**
//     * Mail Kafka Consumer
//     * byte array 역직렬화 후 메일 발송
//     * @param byteCode
//     */
//    @KafkaListener(topics = "mail")
//    private void getMailKafkaConsumer(byte[] byteCode){
//        try {
//            KafkaMailProto.KafkaMailRequest kafkaMail = KafkaMailProto.KafkaMailRequest.parseFrom(byteCode);
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(kafkaMail.getTo());
//            message.setSubject(kafkaMail.getSubject());
//            message.setText(kafkaMail.getText());
//            sendMail(message).get();
//        }
//        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
//            throw new WrongAccessException(WrongAccessException.of.KAFKA_CONNECTION_EXCEPTION);
//        }
//    }
//
//    /**
//     * 비동기 메일 전송
//     * @param message
//     * @return
//     */
//    @Async
//    private Future<Void> sendMail(SimpleMailMessage message){
//        javaMailSender.send(message);
//        return CompletableFuture.completedFuture(null);
//    }
}
