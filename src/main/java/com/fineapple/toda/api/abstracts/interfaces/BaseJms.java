package com.fineapple.toda.api.abstracts.interfaces;

import com.google.protobuf.MessageLite;
import java.util.concurrent.CompletableFuture;

public interface BaseJms {
    /**
     * JmsFcmProto 프로토콜 버퍼를 byte array로 만들어 Jms로 send
     * @param message
     */
    CompletableFuture<Boolean> sendJmsMessage(String destination, MessageLite message);
}
