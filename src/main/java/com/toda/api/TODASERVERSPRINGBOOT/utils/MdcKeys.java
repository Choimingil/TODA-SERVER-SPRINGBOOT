package com.toda.api.TODASERVERSPRINGBOOT.utils;

import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    REQUEST_ID(
            () -> MDC.get("request_id"),
            (request) -> MDC.put("request_id", UUID.randomUUID().toString()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_id"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_id : " + MDC.get("request_id"))
    ),
    REQUEST_CONTEXT_PATH(
            () -> MDC.get("request_context_path"),
            (request) -> MDC.put("request_context_path", request.getContextPath()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_context_path"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_context_path : " + MDC.get("request_context_path"))
    ),
    REQUEST_URL(
            () -> MDC.get("request_url"),
            (request) -> MDC.put("request_url", request.getRequestURI()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_url"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_url : " + MDC.get("request_url"))
    ),
    REQUEST_METHOD(
            () -> MDC.get("request_method"),
            (request) -> MDC.put("request_method", request.getMethod()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_method"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_method : " + MDC.get("request_method"))
    ),
    REQUEST_TIME(
            () -> MDC.get("request_time"),
            (request) -> MDC.put("request_time", new Date().toString()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_time"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_time : " + MDC.get("request_time"))
    ),
    REQUEST_IP(
            () -> MDC.get("request_ip"),
            (request) -> MDC.put("request_ip", request.getRemoteAddr()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_ip"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_ip : " + MDC.get("request_ip"))
    ),
    REQUEST_HEADER(
            () -> MDC.get("request_header"),
            (request) -> MDC.put("request_header", request.getHeader(TokenProvider.HEADER_NAME)),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_header"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_header : " + MDC.get("request_header"))
    ),
    REQUEST_QUERY_STRING(
            () -> MDC.get("request_query_string"),
            (request) -> MDC.put("request_query_string", request.getQueryString()),
            (result) ->  LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            () -> MDC.remove("request_query_string"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_query_string : " + MDC.get("request_query_string"))
    ),
    REQUEST_BODY(
            () -> MDC.get("request_body"),
            (request) -> LoggerFactory.getLogger(MdcKeys.class).error("wrong access"),
            (result) ->  MDC.put("request_body",result.getModel().toString()),
            () -> MDC.remove("request_body"),
            () -> LoggerFactory.getLogger(MdcKeys.class).info("request_body : " + MDC.get("request_body"))
    );

    private final Supplier<String> get;
    private final Consumer<HttpServletRequest> add;
    private final Consumer<BindingResult> addBody;
    private final Runnable remove;
    private final Runnable log;

    public final String get(){
        return get.get();
    }

    public final void add(HttpServletRequest request){
        add.accept(request);
    }

    public final void addBody(BindingResult bindingResult){
        addBody.accept(bindingResult);
    }

    public final void remove(){
        remove.run();
    }

    public final void log(){
        log.run();
    }
}