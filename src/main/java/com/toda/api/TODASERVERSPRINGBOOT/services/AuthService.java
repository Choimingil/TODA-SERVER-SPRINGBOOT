package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateKafka delegateKafka,
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateKafka);
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public String createJwt(String email, String pw) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,pw);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserData userData = getUserInfo(email);
        return createToken(authentication, userData);
    }

    public Map<String,?> getTokenData(String token) {
        UserData userData = getUserInfo(token);

        Map<String,Object> map = new HashMap<>();
        map.put("id", userData.getUserID());
        map.put("pw", userData.getPassword());
        map.put("appPw", userData.getAppPassword());
        return map;
    }
}
