package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authService")
public class AuthService extends AbstractService implements BaseService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public String createJwt(String email, String pw) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,pw);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetail userDetail = getUserInfo(email);
        return createToken(authentication, userDetail);
    }
}
