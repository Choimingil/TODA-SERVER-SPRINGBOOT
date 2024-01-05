package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.enums.LogFields;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(AbstractInterceptor.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;

    protected boolean haveValidHeader(HttpServletRequest request){
        return delegateJwt.isExistHeader(request) && delegateJwt.isValidHeader(request);
    }

    protected String getSubject(HttpServletRequest request) {
        return delegateJwt.getSubject(request);
    }

    protected String getToken(HttpServletRequest request) {
        return delegateJwt.getToken(request);
    }

    protected UserData decodeToken(String token) {
        return delegateJwt.decodeToken(token);
    }
}
