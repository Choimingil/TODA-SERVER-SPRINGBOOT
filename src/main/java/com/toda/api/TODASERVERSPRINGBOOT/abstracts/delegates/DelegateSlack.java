package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseSlack;
import com.toda.api.TODASERVERSPRINGBOOT.enums.SlackKeys;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.JmsSlackProto;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class DelegateSlack implements BaseSlack, InitializingBean {
    private final SlackApi slackApi;
    private final SlackAttachment slackAttachment;
    private final SlackMessage slackMessage;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final Set<SlackKeys> slackKeysEnumSet = EnumSet.allOf(SlackKeys.class);

    /**
     * Slack Jms Consumer
     * byte array 역직렬화 후 메일 발송
     * @param byteCode
     */
    @JmsListener(destination = "slack")
    private void getSlackJmsConsumer(byte[] byteCode){
        try {
            JmsSlackProto.JmsSlackRequest jmsSlack = JmsSlackProto.JmsSlackRequest.parseFrom(byteCode);
            slackAttachment.setTitleLink(jmsSlack.getTitleLink());
            slackAttachment.setFields(getSlackFields(jmsSlack.getSlackFieldsMap()));
            slackAttachment.setText(jmsSlack.getStackTrace());
            send().get();
        }
        catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.MQ_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기로 슬랙 메시지 전송
     * @return
     */
    private CompletableFuture<Void> send(){
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        return CompletableFuture.runAsync(()->slackApi.call(slackMessage),taskExecutor);
    }

    /**
     * JmsSlackRequest에서 가져온 값들을 SlackField로 변환
     * 키 : slackTitle, 벨류 : 각각의 값
     * @param map
     * @return
     */
    private List<SlackField> getSlackFields(Map<String,String> map){
        return map.entrySet().stream()
                .map(entry -> new SlackField().setTitle(entry.getKey()).setValue(entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        slackKeysEnumSet.remove(SlackKeys.REQUEST_BODY);
    }

    @PreDestroy
    public void shutdown() {
        taskExecutor.shutdown();
    }
}
