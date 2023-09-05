package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaMailProto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class MailProvider {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;
    private final JavaMailSender javaMailSender;

    /**
     * Mail Kafka Producer
     * KafkaMailProto 프로토콜 버퍼를 byte array로 만들어 kafka로 send
     * @param to
     * @param subject
     * @param text
     */
    public void getMailKafkaProducer(String to, String subject, String text){
        KafkaMailProto.KafkaMail params = KafkaMailProto.KafkaMail.newBuilder()
                .setTo(to)
                .setSubject(subject)
                .setText(text)
                .build();
        kafkaTemplate.send("mail", params.toByteArray());
//        kafkaTemplate.send(new ProducerRecord<>("mail", params.toByteArray()));
    }

    /**
     * Mail Kafka Consumer
     * byte array 역직렬화 후 메일 발송
     * @param byteCode
     */
    @KafkaListener(topics = "mail")
    private void getMailKafkaConsumer(byte[] byteCode){
        try {
            KafkaMailProto.KafkaMail kafkaMail = KafkaMailProto.KafkaMail.parseFrom(byteCode);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(kafkaMail.getTo());
            message.setSubject(kafkaMail.getSubject());
            message.setText(kafkaMail.getText());

            sendMail(message).get();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.KAFKA_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 메일 전송
     * @param message
     * @return
     */
    @Async
    private Future<Void> sendMail(SimpleMailMessage message){
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(null);
    }
}
