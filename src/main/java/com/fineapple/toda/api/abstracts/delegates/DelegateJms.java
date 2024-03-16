package com.fineapple.toda.api.abstracts.delegates;

import com.google.protobuf.MessageLite;
import com.fineapple.toda.api.abstracts.interfaces.BaseJms;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class DelegateJms implements BaseJms {
    private final JmsTemplate jmsTemplate;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public CompletableFuture<Boolean> sendJmsMessage(String destination, MessageLite message) {
        return CompletableFuture.supplyAsync(()->{
            jmsTemplate.convertAndSend(destination,message.toByteArray());
            return true;
        },taskExecutor);
    }

    @PreDestroy
    public void shutdown() {
        taskExecutor.shutdown();
    }
}
