package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.google.protobuf.MessageLite;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public final class DelegateKafka implements BaseKafka {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;
    private final ThreadPoolTaskExecutor taskExecutor;
    @Override
    public CompletableFuture<Boolean> getKafkaProducer(String topic, MessageLite message) {
        return CompletableFuture.supplyAsync(()->{
            kafkaTemplate.send(topic, message.toByteArray());
            return true;
        },taskExecutor);
    }
}
