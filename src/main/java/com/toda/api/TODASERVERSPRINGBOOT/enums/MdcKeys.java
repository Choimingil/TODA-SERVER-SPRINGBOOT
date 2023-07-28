package com.toda.api.TODASERVERSPRINGBOOT.enums;

import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.UUID;
import java.util.function.*;

@RequiredArgsConstructor
public enum MdcKeys {
    /**
     * - request_id : 로그 아이디
     * - request_context_path : 요청 path
     * - request_url : 요청한 url
     * - request_method : url method
     * - request_time : 요청 시간
     * - request_ip : 요청한 ip 주소
     * - request_header : 요청 헤더
     * - request_query_string : 요청 쿼리 스트링
     * - request_body : 요청 바디 (컨트롤러에서 벨리데이션 이후 추가)
     */

    REQUEST_ID("request_id", MDC::get, MDC::put, MDC::remove),
    REQUEST_CONTEXT_PATH("request_context_path", MDC::get, MDC::put, MDC::remove),
    REQUEST_URL("request_url", MDC::get, MDC::put, MDC::remove),
    REQUEST_METHOD("request_method", MDC::get, MDC::put, MDC::remove),
    REQUEST_TIME("request_time", MDC::get, MDC::put, MDC::remove),
    REQUEST_IP("request_ip", MDC::get, MDC::put, MDC::remove),
    REQUEST_HEADER("request_header", MDC::get, MDC::put, MDC::remove),
    REQUEST_QUERY_STRING("request_query_string", MDC::get, MDC::put, MDC::remove),
    REQUEST_BODY("request_body", MDC::get, MDC::put, MDC::remove);

    private final String title;
    private final Function<String, String> get;
    private final BiConsumer<String,String> add;
    private final Consumer<String> remove;
    public final String get(){
        return get.apply(title);
    }
    public final void add(HttpServletRequest request, Logger logger){
        String val = switch (title) {
            case "request_id" -> UUID.randomUUID().toString();
            case "request_context_path" -> request.getContextPath();
            case "request_url" -> request.getRequestURI();
            case "request_method" -> request.getMethod();
            case "request_time" -> Instant.now().toString();
            case "request_ip" -> request.getRemoteAddr();
            case "request_header" -> request.getHeader(TokenProvider.HEADER_NAME);
            case "request_query_string" -> request.getQueryString();
            default -> "";
        };
        add.accept(title,val);
        getLog(logger);
    }
    public final void add(BindingResult bindingResult, Logger logger){
        add.accept(title, bindingResult.getModel().toString());
        getLog(logger);
    }
    public final void remove(Logger logger){
        remove.accept(title);
        getLog(logger);
    }

    private void getLog(Logger logger){
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(" : ");
        sb.append(MDC.get(title));
        logger.info(sb.toString());
    }
}