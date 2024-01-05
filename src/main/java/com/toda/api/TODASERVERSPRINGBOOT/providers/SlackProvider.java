package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.enums.SlackKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SlackProvider extends AbstractProvider implements BaseProvider, InitializingBean {
    private final SlackApi slackApi;
    private final SlackAttachment slackAttachment;
    private final SlackMessage slackMessage;
    private final Set<SlackKeys> slackKeysEnumSet = EnumSet.allOf(SlackKeys.class);

    @Override
    public void afterPropertiesSet() {
        slackKeysEnumSet.remove(SlackKeys.REQUEST_BODY);
    }

//    /**
//     * Slack Kafka Consumer
//     * byte array 역직렬화 후 메일 발송
//     * @param byteCode
//     */
//    @KafkaListener(topics = "slack")
//    private void getSlackKafkaConsumer(byte[] byteCode){
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

    // Spring Container Level
    public void doSlack(HttpServletRequest request, Exception exception) {
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setFields(getSlackFields(request));

        try {
            send(exception).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WrongAccessException(WrongAccessException.of.SEND_SLACK_EXCEPTION);
        }
    }

    // Filter Level
    public void doSlack(Exception exception) {
        slackAttachment.setTitleLink(MDC.get("request_context_path"));
        slackAttachment.setFields(getSlackFields());

        try {
            send(exception).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WrongAccessException(WrongAccessException.of.SEND_SLACK_EXCEPTION);
        }
    }

    @Async
    private CompletableFuture<Void> send(Exception e){
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackApi.call(slackMessage);
        return CompletableFuture.completedFuture(null);
    }

    private List<SlackField> getSlackFields(HttpServletRequest request){
        return slackKeysEnumSet.stream()
                .map(keys -> keys.addRequest(request))
                .collect(Collectors.toList());
    }

    private List<SlackField> getSlackFields(){
        slackKeysEnumSet.add(SlackKeys.REQUEST_BODY);
        return slackKeysEnumSet.stream()
                .map(SlackKeys::addMdc)
                .collect(Collectors.toList());
    }
}
