package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import java.util.concurrent.CompletableFuture;

public interface BaseKafka {
    /**
     * Fcm Kafka Producer
     * KafkaFcmProto 프로토콜 버퍼를 byte array로 만들어 kafka로 send
     * @param message
     */
    CompletableFuture<Boolean> getKafkaProducer(String topic, com.google.protobuf.MessageLite message);
}
