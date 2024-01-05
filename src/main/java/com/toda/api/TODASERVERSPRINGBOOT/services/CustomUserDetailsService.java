package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UserProvider;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService extends AbstractService implements BaseService, UserDetailsService {
    private final UserProvider userProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserData userdata = userProvider.getUserInfo(email);
        return User.builder()
                .username(userdata.getEmail())
                .password(passwordEncoder.encode(userdata.getPassword()))
                .roles("USER").build();
    }
}