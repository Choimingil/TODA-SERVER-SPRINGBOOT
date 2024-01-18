package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected UserDetail decodeToken(String token) {
        return delegateJwt.decodeToken(token);
    }
}
