package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class CurlProvider {
    private static final Logger logger = LoggerFactory.getLogger(CurlProvider.class);
    @Autowired
    private SlackApi slackApi;
    @Autowired
    private SlackAttachment slackAttachment;
    @Autowired
    private SlackMessage slackMessage;

    @Async
    public void sendSlack(Exception e) {
        slackAttachment.setTitleLink(MDC.get("request_context_path"));
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));

        slackAttachment.setFields(
                List.of(
                        new SlackField().setTitle("Request URL").setValue(MDC.get("request_url")),
                        new SlackField().setTitle("Request Method").setValue(MDC.get("request_method")),
                        new SlackField().setTitle("Request Time").setValue(MDC.get("request_time")),
                        new SlackField().setTitle("Request IP").setValue(MDC.get("request_ip")),
                        new SlackField().setTitle("Request header").setValue(MDC.get("request_header")),
                        new SlackField().setTitle("Request Query String").setValue(MDC.get("request_query_string")),
                        new SlackField().setTitle("Request Body").setValue(MDC.get("request_body"))
                )
        );

        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackApi.call(slackMessage);
    }

}
