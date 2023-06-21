package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class CurlProvider {
    @Autowired
    private SlackApi slackApi;
    @Autowired
    private SlackAttachment slackAttachment;
    @Autowired
    private SlackMessage slackMessage;

    @Async
    public void sendSlack(HttpServletRequest request,Exception e) {
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));

        slackAttachment.setFields(
                List.of(
                        new SlackField().setTitle("Request URL").setValue(String.valueOf(request.getRequestURL())),
                        new SlackField().setTitle("Request Method").setValue(request.getMethod()),
                        new SlackField().setTitle("Request Time").setValue(new Date().toString()),
                        new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr())
                        ,new SlackField().setTitle("Request header").setValue(request.getHeader("x-access-token"))
                )
        );

        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackApi.call(slackMessage);
    }

}
