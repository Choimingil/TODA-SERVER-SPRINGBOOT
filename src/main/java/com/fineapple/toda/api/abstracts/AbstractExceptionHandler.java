package com.fineapple.toda.api.abstracts;

import com.fineapple.toda.api.abstracts.delegates.DelegateJms;
import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.models.protobuffers.JmsSlackProto;
import com.fineapple.toda.api.models.responses.FailResponse;
import com.fineapple.toda.api.abstracts.interfaces.BaseExceptionHandler;
import com.fineapple.toda.api.enums.SlackKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractExceptionHandler implements BaseExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(AbstractExceptionHandler.class);
    private final Set<SlackKeys> slackKeysEnumSet = EnumSet.allOf(SlackKeys.class);

    /* Delegate Class */
    private final DelegateJms delegateJms;

    @Value("${slack.send.enable}")
    private boolean isSlackEnable;

    @Override
    public Map<String,?> getErrorSpringContainer(Exception e, int elementCode, String errorMessage){
        logger.error(e.getMessage());
        logger.error(Arrays.toString(e.getStackTrace()));

        if(isSlackEnable){
            String titleLink = MDC.get("request_context_path");
            slackKeysEnumSet.add(SlackKeys.REQUEST_BODY);
            Map<String, String> slackFields = slackKeysEnumSet.stream()
                    .collect(Collectors.toMap(
                            slackKeys -> slackKeys.slackTitle==null?"empty key":slackKeys.slackTitle,
                            slackKeys -> MDC.get(slackKeys.mdcTitle)==null?"empty value":MDC.get(slackKeys.mdcTitle)
                    ));

            JmsSlackProto.JmsSlackRequest jmsSlackRequest = JmsSlackProto.JmsSlackRequest.newBuilder()
                    .setTitleLink(titleLink)
                    .putAllSlackFields(slackFields)
                    .setStackTrace(Arrays.toString(e.getStackTrace()))
                    .build();
            delegateJms.sendJmsMessage("slack",jmsSlackRequest);
        }

        return new FailResponse.Builder(elementCode, errorMessage)
                .build()
                .getResponse();
    }

    @Override
    public Map<String,?> getErrorFilter(HttpServletRequest request, Exception e, int elementCode, String errorMessage){
        logger.error(e.getMessage());
        logger.error(Arrays.toString(e.getStackTrace()));

        if(isSlackEnable){
            String titleLink = request.getContextPath();
            JmsSlackProto.JmsSlackRequest jmsSlackRequest = JmsSlackProto.JmsSlackRequest.newBuilder()
                    .setTitleLink(titleLink)
                    .putSlackFields("Request URL",request.getRequestURI())
                    .putSlackFields("Request Method",request.getMethod())
                    .putSlackFields("Request Time",Instant.now().toString())
                    .putSlackFields("Request IP",request.getRemoteAddr())
                    .putSlackFields("Request Header",request.getHeader(DelegateJwt.HEADER_NAME)==null ? "No Header" : request.getHeader(DelegateJwt.HEADER_NAME))
                    .putSlackFields("Request Query String",request.getQueryString()==null ? "No Query String" : request.getQueryString())
                    .setStackTrace(Arrays.toString(e.getStackTrace()))
                    .build();
            delegateJms.sendJmsMessage("slack",jmsSlackRequest);
        }

        return new FailResponse.Builder(elementCode, errorMessage)
                .build()
                .getResponse();
    }

    @Override
    public String getErrorMsg(Exception e){
        StringBuilder sb = new StringBuilder();
        sb.append("exception type :");
        sb.append(e.getClass());
        sb.append(" \nexception text : ");
        sb.append(e.getMessage());
        sb.append(" \nexception stack trace : ");
        sb.append(Arrays.toString(e.getStackTrace()));
        return sb.toString();
    }
}
