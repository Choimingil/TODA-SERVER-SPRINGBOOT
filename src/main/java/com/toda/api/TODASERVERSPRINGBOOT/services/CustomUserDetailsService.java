package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.providers.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
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
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisProvider redisProvider;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 저장된 유저 정보
        UserInfoAllDao userInfoAllDao = redisProvider.getUserInfo(email);

        // 비밀번호가 해싱되어있지 않은 경우 인코딩 진행
        if(userInfoAllDao.getPassword().length() < 25){
            String encodedPassword = passwordEncoder.encode(userInfoAllDao.getPassword());
            authRepository.setUserPasswordEncoded(userInfoAllDao.getEmail(), encodedPassword);
            return User.builder()
                    .username(email)
                    .password(encodedPassword)
                    .roles("USER").build();
        }
        // 해싱되어있는 경우 그대로 검증
        else return User.builder()
                .username(email)
                .password(userInfoAllDao.getPassword())
                .roles("USER").build();
    }
}