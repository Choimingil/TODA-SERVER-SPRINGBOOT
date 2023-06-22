package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class MdcProvider {
    private static final Logger logger = LoggerFactory.getLogger(MdcProvider.class);

    // Singleton Pattern
    private static MdcProvider mdcProvider = null;
    public static MdcProvider getInstance(){
        if(mdcProvider == null){
            mdcProvider = new MdcProvider();
        }
        return mdcProvider;
    }

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
     **/

    public void setMdc(HttpServletRequest request){
        MDC.put("request_id", UUID.randomUUID().toString());
        logger.info("request_id : " + MDC.get("request_id"));

        MDC.put("request_context_path", request.getContextPath());
        logger.info("request_context_path : " + MDC.get("request_context_path"));

        MDC.put("request_url", request.getRequestURI());
        logger.info("request_url : " + MDC.get("request_url"));

        MDC.put("request_method", request.getMethod());
        logger.info("request_method : " + MDC.get("request_method"));

        MDC.put("request_time", new Date().toString());
        logger.info("request_time : " + MDC.get("request_time"));

        MDC.put("request_ip", request.getRemoteAddr());
        logger.info("request_ip : " + MDC.get("request_ip"));

        MDC.put("request_header", request.getHeader(TokenProvider.HEADER_NAME));
        logger.info("request_header : " + MDC.get("request_header"));

        MDC.put("request_query_string", request.getQueryString());
        logger.info("request_query_string : " + MDC.get("request_query_string"));
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");
        MDC.put("request_body",bindingResult.getModel().toString());
        logger.info("request_body : " + MDC.get("request_body"));
    }

    public void removeMdc(){
        MDC.remove("request_context_path");
        MDC.remove("request_url");
        MDC.remove("request_method");
        MDC.remove("request_time");
        MDC.remove("request_ip");
        MDC.remove("request_header");
        MDC.remove("request_query_string");
        MDC.remove("request_body");
    }

}
