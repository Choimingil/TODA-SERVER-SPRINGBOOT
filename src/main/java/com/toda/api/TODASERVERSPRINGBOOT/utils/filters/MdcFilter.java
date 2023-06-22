package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class MdcFilter extends OncePerRequestFilter {
    // Singleton Pattern
    private static MdcFilter mdcFilter = null;
    public static MdcFilter getInstance(){
        if(mdcFilter == null){
            mdcFilter = new MdcFilter();
        }
        return mdcFilter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            // 3. 요청 정보 mdc에 저장
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
            logger.info("3. mdc 저장");

            final UUID uuid = UUID.randomUUID();
            MDC.put("request_id", uuid.toString());
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

            filterChain.doFilter(request,response);
        }
        catch(ValidationException e){
            logger.error(e.getMessage());
            FilterExceptionHandler.getInstance().setErrorResponse(e.getCode(),e.getMessage(),response);
        }
    }
}
