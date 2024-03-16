package com.fineapple.toda.api.enums;

import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackField;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum SlackKeys {
    REQUEST_URL("Request URL", "request_url", SlackKeys::setField, SlackKeys::setField),
    REQUEST_METHOD("Request Method", "request_method", SlackKeys::setField, SlackKeys::setField),
    REQUEST_TIME("Request Time", "request_time", SlackKeys::setField, SlackKeys::setField),
    REQUEST_IP("Request IP", "request_ip", SlackKeys::setField, SlackKeys::setField),
    REQUEST_HEADER("Request Header", "request_header", SlackKeys::setField, SlackKeys::setField),
    REQUEST_QUERY_STRING("Request Query String", "request_query_string", SlackKeys::setField, SlackKeys::setField),
    REQUEST_BODY("Request Body", "request_body", SlackKeys::setField, SlackKeys::setField);

    public final String slackTitle;
    public final String mdcTitle;
    private final BiFunction<String, String, SlackField> addRequest;
    private final BiFunction<String, String, SlackField> addMdc;
    public final SlackField addRequest(HttpServletRequest request){
        String val = switch (mdcTitle) {
            case "request_url" -> request.getRequestURI();
            case "request_method" -> request.getMethod();
            case "request_time" -> Instant.now().toString();
            case "request_ip" -> request.getRemoteAddr();
            case "request_header" -> request.getHeader(DelegateJwt.HEADER_NAME);
            case "request_query_string" -> request.getQueryString();
            default -> "";
        };
        return addRequest.apply(slackTitle,val);
    }
    public final SlackField addMdc(){
        return addMdc.apply(slackTitle,MDC.get(mdcTitle));
    }

    private static SlackField setField(String slackTitle, String value){
        return new SlackField().setTitle(slackTitle).setValue(value);
    }
}
