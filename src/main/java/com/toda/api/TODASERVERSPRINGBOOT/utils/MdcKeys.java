package com.toda.api.TODASERVERSPRINGBOOT.utils;

import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.extenders.MdcKeysExtender;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.UUID;

public enum MdcKeys implements MdcKeysExtender {
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

    REQUEST_ID{
        public void add(HttpServletRequest request){ MDC.put("request_id",UUID.randomUUID().toString()); }
        public void remove(){ MDC.remove("request_id"); }
        public void log(){ logger.info("request_id : " + MDC.get("request_id")); }
    },
    REQUEST_CONTEXT_PATH{
        public void add(HttpServletRequest request){ MDC.put("request_context_path", request.getContextPath()); }
        public void remove(){ MDC.remove("request_context_path"); }
        public void log(){ logger.info("request_context_path : " + MDC.get("request_context_path")); }
    },
    REQUEST_URL{
        public void add(HttpServletRequest request){ MDC.put("request_url", request.getRequestURI()); }
        public void remove(){ MDC.remove("request_url"); }
        public void log(){ logger.info("request_url : " + MDC.get("request_url")); }
    },
    REQUEST_METHOD{
        public void add(HttpServletRequest request){ MDC.put("request_method", request.getMethod()); }
        public void remove(){ MDC.remove("request_method"); }
        public void log(){ logger.info("request_method : " + MDC.get("request_method")); }
    },
    REQUEST_TIME{
        public void add(HttpServletRequest request){ MDC.put("request_time", new Date().toString()); }
        public void remove(){ MDC.remove("request_time"); }
        public void log(){ logger.info("request_time : " + MDC.get("request_time")); }
    },
    REQUEST_IP{
        public void add(HttpServletRequest request){ MDC.put("request_ip", request.getRemoteAddr()); }
        public void remove(){ MDC.remove("request_ip"); }
        public void log(){ logger.info("request_ip : " + MDC.get("request_ip")); }
    },
    REQUEST_HEADER{
        public void add(HttpServletRequest request){ MDC.put("request_header", request.getHeader(TokenProvider.HEADER_NAME)); }
        public void remove(){ MDC.remove("request_header"); }
        public void log(){ logger.info("request_header : " + MDC.get("request_header")); }
    },
    REQUEST_QUERY_STRING{
        public void add(HttpServletRequest request){ MDC.put("request_query_string", request.getQueryString()); }
        public void remove(){ MDC.remove("request_query_string"); }
        public void log(){ logger.info("request_query_string : " + MDC.get("request_query_string")); }
    };

    protected final Logger logger = LoggerFactory.getLogger(MdcKeys.class);
}