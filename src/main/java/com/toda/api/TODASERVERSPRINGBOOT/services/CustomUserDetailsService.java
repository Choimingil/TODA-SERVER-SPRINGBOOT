package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
public class CustomUserDetailsService extends AbstractService implements BaseService, UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateJms delegateJms,
            PasswordEncoder passwordEncoder
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateJms);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetail userDetail = getUserInfo(email);
        return User.builder()
                .username(userDetail.getUser().getEmail())
                .password(passwordEncoder.encode(userDetail.getUser().getPassword()))
                .roles("USER").build();
    }
}