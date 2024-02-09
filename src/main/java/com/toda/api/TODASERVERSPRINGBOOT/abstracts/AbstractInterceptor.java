package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateUri;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateUserAuth;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.JwtHeader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class AbstractInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(AbstractInterceptor.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;
    private final DelegateUri delegateUri;

    protected boolean haveValidHeader(HttpServletRequest request){
        if(delegateUri.isValidPass(request)) return false;
        return delegateJwt.isExistHeader(request) && delegateJwt.isValidHeader(request);
    }
    protected JwtHeader decodeToken(HttpServletRequest request){
        String token = request.getHeader(DelegateJwt.HEADER_NAME);
        return delegateJwt.decodeToken(token);
    }

    protected String getToken(HttpServletRequest request) {
        return delegateJwt.getToken(request);
    }
}
