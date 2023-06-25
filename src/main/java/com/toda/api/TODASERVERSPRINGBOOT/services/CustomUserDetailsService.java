package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.utils.plugins.ValidateWithRedis;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService extends AbstractService implements BaseService, UserDetailsService, ValidateWithRedis {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(!isExistRedis(email)) setRedis(email);
        UserInfoAllDao userInfoAllDao = getRedis(email);

        // 비밀번호가 해싱되어있지 않은 경우 인코딩 진행
        if(userInfoAllDao.getPassword().length() < 25){
            String encodedPassword = passwordEncoder.encode(userInfoAllDao.getPassword());
            authRepository.setUserPasswordEncoded(userInfoAllDao.getEmail(),encodedPassword);
            return (UserDetails)new User(
                    email, encodedPassword, AuthorityUtils.createAuthorityList("USER")
            );
        }
        // 해싱되어있는 경우 그대로 검증
        else return (UserDetails)new User(
                email, userInfoAllDao.getPassword(), AuthorityUtils.createAuthorityList("USER")
        );
    }

    @Override
    public ValueOperations<String, Object> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    @Override
    public AuthRepository getRepository() {
        return authRepository;
    }
}