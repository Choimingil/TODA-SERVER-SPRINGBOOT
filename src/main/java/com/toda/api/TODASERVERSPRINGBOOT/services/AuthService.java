package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateFile;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateStatus;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UserProvider;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("authService")
@RequiredArgsConstructor
public class AuthService extends AbstractService implements BaseService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final UserProvider userProvider;

//    public AuthService(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, UserProvider userProvider){
//        super(delegateDateTime,delegateFile,delegateStatus);
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//        this.tokenProvider = tokenProvider;
//        this.userProvider = userProvider;
//    }

    public String createJwt(String email, String pw) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,pw);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserData userData = userProvider.getUserInfo(email);
        return tokenProvider.createToken(authentication, userData);
    }

    public Map<String,?> getTokenData(String token) {
        UserData userData = userProvider.getUserInfo(token);

        Map<String,Object> map = new HashMap<>();
        map.put("id", userData.getUserID());
        map.put("pw", userData.getPassword());
        map.put("appPw", userData.getAppPassword());
        return map;
    }
}
