package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseMail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaMailProto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public final class DelegateMail implements BaseMail {
    private final JavaMailSender javaMailSender;
    private final ThreadPoolTaskExecutor taskExecutor;

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

    @Override
    public Future<Void> sendMail(SimpleMailMessage message) {
        return CompletableFuture.runAsync(()->javaMailSender.send(message),taskExecutor);
    }
}
