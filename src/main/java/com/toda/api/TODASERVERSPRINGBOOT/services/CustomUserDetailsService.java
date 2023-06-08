package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Redis 에 유저 정보 존재하는지 확인
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserInfoAllDAO userInfoAllDAO = (UserInfoAllDAO) valueOperations.get(email);

        // 유저 정보가 없다면 DB에 접근해서 추가
        if(userInfoAllDAO == null){
            userInfoAllDAO = authRepository.getUserInfoAll(email);
            valueOperations.set(email,userInfoAllDAO);
        }

        // 비밀번호가 해싱되어있지 않은 경우 인코딩 진행
        if(userInfoAllDAO.getPassword().length() < 25){
            String encodedPassword = passwordEncoder.encode(userInfoAllDAO.getPassword());
            authRepository.setUserPasswordEncoded(userInfoAllDAO.getEmail(),encodedPassword);
            return (UserDetails)new User(
                    email, encodedPassword, AuthorityUtils.createAuthorityList("USER")
            );
        }
        // 해싱되어있는 경우 그대로 검증
        else return (UserDetails)new User(
                email, userInfoAllDAO.getPassword(), AuthorityUtils.createAuthorityList("USER")
        );
    }


}