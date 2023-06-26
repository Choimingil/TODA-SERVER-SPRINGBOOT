package com.toda.api.TODASERVERSPRINGBOOT.utils;

import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.extenders.SlackKeysExtender;
import jakarta.servlet.http.HttpServletRequest;
import net.gpedro.integrations.slack.SlackField;
import org.slf4j.MDC;

import java.util.Date;

public enum SlackKeys implements SlackKeysExtender {
    REQUEST_URL{
        @Override public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request URL").setValue(request.getRequestURI()); }
        @Override public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request URL").setValue(MDC.get("request_url")); }
    },
    REQUEST_METHOD{
        @Override public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request Method").setValue(request.getMethod()); }
        @Override public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request Method").setValue(MDC.get("request_method")); }
    },
    REQUEST_TIME{
        @Override public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request Time").setValue(new Date().toString()); }
        @Override public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request Time").setValue(MDC.get("request_time")); }
    },
    REQUEST_IP{
        @Override public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()); }
        @Override public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request IP").setValue(MDC.get("request_ip")); }
    },
    REQUEST_HEADER{
        @Override public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request header").setValue(request.getHeader(TokenProvider.HEADER_NAME)); }
        @Override public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request header").setValue(MDC.get("request_header")); }
    },
    REQUEST_QUERY_STRING{
        @Override public SlackField setFieldWithRequest(HttpServletRequest request){ return new SlackField().setTitle("Request Query String").setValue(request.getQueryString()); }
        @Override public SlackField setFieldWithMdc(){ return new SlackField().setTitle("Request Query String").setValue(MDC.get("request_query_string")); }
    }
}
