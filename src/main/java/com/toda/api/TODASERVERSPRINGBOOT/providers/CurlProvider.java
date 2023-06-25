package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class CurlProvider extends AbstractProvider implements BaseProvider {
    private final MdcProvider mdcProvider;
    private final SlackApi slackApi;
    private final SlackAttachment slackAttachment;
    private final SlackMessage slackMessage;

    @Override
    public void afterPropertiesSet() {
        beforeSet();
    }

    private void beforeSet(){
        slackAttachment.setFallback("Error");
        slackAttachment.setColor("danger");
        slackAttachment.setTitle("Error Directory");

        slackMessage.setIcon(":ghost:");
        slackMessage.setText("Error Detected");
        slackMessage.setUsername("TODA Error Catcher");
    }

    private void send(Exception e){
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackApi.call(slackMessage);
        mdcProvider.removeMdc();
    }

    public void sendSlackWithNoMdc(HttpServletRequest request, Exception e) {
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setFields(
                List.of(
                        new SlackField().setTitle("Request URL").setValue(request.getRequestURI()),
                        new SlackField().setTitle("Request Method").setValue(request.getMethod()),
                        new SlackField().setTitle("Request Time").setValue(new Date().toString()),
                        new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()),
                        new SlackField().setTitle("Request header").setValue(request.getHeader(TokenProvider.HEADER_NAME)),
                        new SlackField().setTitle("Request Query String").setValue(request.getQueryString())
                )
        );

        send(e);
    }

    @Async
    public void sendSlackWithMdc(Exception e) {
        slackAttachment.setTitleLink(MDC.get("request_context_path"));
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

        send(e);
    }




}
