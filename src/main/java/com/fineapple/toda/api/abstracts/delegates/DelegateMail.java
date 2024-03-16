package com.fineapple.toda.api.abstracts.delegates;

import com.fineapple.toda.api.abstracts.interfaces.BaseMail;
import com.fineapple.toda.api.exceptions.WrongAccessException;
import com.fineapple.toda.api.models.protobuffers.JmsMailProto;
import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
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
     * Mail Jms Consumer
     * byte array 역직렬화 후 메일 발송
     * @param byteCode
     */
    @JmsListener(destination = "mail")
    private void getMailJmsConsumer(byte[] byteCode){
        try {
            JmsMailProto.JmsMailRequest jmsMail = JmsMailProto.JmsMailRequest.parseFrom(byteCode);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(jmsMail.getTo());
            message.setSubject(jmsMail.getSubject());
            message.setText(jmsMail.getText());
            sendMail(message).get();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.MQ_CONNECTION_EXCEPTION);
        }
    }

    @Override
    public Future<Void> sendMail(SimpleMailMessage message) {
        return CompletableFuture.runAsync(()->javaMailSender.send(message),taskExecutor);
    }

    @PreDestroy
    public void shutdown() {
        taskExecutor.shutdown();
    }
}
