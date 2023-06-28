package com.toda.api.TODASERVERSPRINGBOOT.utils;

import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackField;
import org.slf4j.MDC;

import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum SlackKeys {
    REQUEST_URL(
            (request) -> new SlackField().setTitle("Request URL").setValue(request.getRequestURI()),
            () -> new SlackField().setTitle("Request URL").setValue(MDC.get("request_url"))
    ),
    REQUEST_METHOD(
            (request) -> new SlackField().setTitle("Request Method").setValue(request.getMethod()),
            () -> new SlackField().setTitle("Request Method").setValue(MDC.get("request_method"))
    ),
    REQUEST_TIME(
            (request) -> new SlackField().setTitle("Request Time").setValue(new Date().toString()),
            () -> new SlackField().setTitle("Request Time").setValue(MDC.get("request_time"))
    ),
    REQUEST_IP(
            (request) -> new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()),
            () -> new SlackField().setTitle("Request IP").setValue(MDC.get("request_ip"))
    ),
    REQUEST_HEADER(
            (request) -> new SlackField().setTitle("Request Header").setValue(request.getHeader(TokenProvider.HEADER_NAME)),
            () -> new SlackField().setTitle("Request Header").setValue(MDC.get("request_header"))
    ),
    REQUEST_QUERY_STRING(
            (request) -> new SlackField().setTitle("Request Query String").setValue(request.getQueryString()),
            () -> new SlackField().setTitle("Request Query String").setValue(MDC.get("request_query_string"))
    );

    private final Function<HttpServletRequest,SlackField> addRequest;
    private final Supplier<SlackField> addMdc;

    public final SlackField addRequest(HttpServletRequest request){
        return addRequest.apply(request);
    }

    public final SlackField addMdc(){
        return addMdc.get();
    }
}
