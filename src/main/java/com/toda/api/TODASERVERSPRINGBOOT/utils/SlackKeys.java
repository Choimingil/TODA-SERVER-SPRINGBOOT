package com.toda.api.TODASERVERSPRINGBOOT.utils;

import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.extenders.SlackKeysExtender;
import jakarta.servlet.http.HttpServletRequest;
import net.gpedro.integrations.slack.SlackField;
import org.slf4j.MDC;

import java.util.Date;

public enum SlackKeys implements SlackKeysExtender {
    REQUEST_URL{
        public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request URL").setValue(request.getRequestURI()); }
        public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request URL").setValue(MDC.get("request_url")); }
    },
    REQUEST_METHOD{
        public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request Method").setValue(request.getMethod()); }
        public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request Method").setValue(MDC.get("request_method")); }
    },
    REQUEST_TIME{
        public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request Time").setValue(new Date().toString()); }
        public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request Time").setValue(MDC.get("request_time")); }
    },
    REQUEST_IP{
        public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()); }
        public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request IP").setValue(MDC.get("request_ip")); }
    },
    REQUEST_HEADER{
        public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request header").setValue(request.getHeader(TokenProvider.HEADER_NAME)); }
        public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request header").setValue(MDC.get("request_header")); }
    },
    REQUEST_QUERY_STRING{
        public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request Query String").setValue(request.getQueryString()); }
        public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request Query String").setValue(MDC.get("request_query_string")); }
    }
}
