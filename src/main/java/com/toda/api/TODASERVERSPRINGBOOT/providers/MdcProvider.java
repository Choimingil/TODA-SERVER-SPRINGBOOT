package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class MdcProvider extends AbstractProvider implements BaseProvider {
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

    public void setMdc(HttpServletRequest request){
        MDC.put("request_id", UUID.randomUUID().toString());
        MDC.put("request_context_path", request.getContextPath());
        MDC.put("request_url", request.getRequestURI());
        MDC.put("request_method", request.getMethod());
        MDC.put("request_time", new Date().toString());
        MDC.put("request_ip", request.getRemoteAddr());
        MDC.put("request_header", request.getHeader(TokenProvider.HEADER_NAME));
        MDC.put("request_query_string", request.getQueryString());
        getMdcLogs();
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");
        MDC.put("request_body",bindingResult.getModel().toString());
        logger.info("request_body : " + MDC.get("request_body"));
    }

    public void removeMdc(){
        MDC.remove("request_id");
        MDC.remove("request_context_path");
        MDC.remove("request_url");
        MDC.remove("request_method");
        MDC.remove("request_time");
        MDC.remove("request_ip");
        MDC.remove("request_header");
        MDC.remove("request_query_string");
        MDC.remove("request_body");
        getMdcLogs();
    }

    private void getMdcLogs(){
        logger.info("request_id : " + MDC.get("request_id"));
        logger.info("request_context_path : " + MDC.get("request_context_path"));
        logger.info("request_url : " + MDC.get("request_url"));
        logger.info("request_method : " + MDC.get("request_method"));
        logger.info("request_time : " + MDC.get("request_time"));
        logger.info("request_ip : " + MDC.get("request_ip"));
        logger.info("request_header : " + MDC.get("request_header"));
        logger.info("request_query_string : " + MDC.get("request_query_string"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
