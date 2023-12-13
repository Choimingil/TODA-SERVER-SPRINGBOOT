package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaMailProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class MailProvider extends AbstractProvider implements BaseProvider {
    private final JavaMailSender javaMailSender;

    /**
     * Mail Kafka Consumer
     * byte array 역직렬화 후 메일 발송
     * @param byteCode
     */
    @KafkaListener(topics = "mail")
    private void getMailKafkaConsumer(byte[] byteCode){
        try {
            KafkaMailProto.KafkaMailRequest kafkaMail = KafkaMailProto.KafkaMailRequest.parseFrom(byteCode);
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
