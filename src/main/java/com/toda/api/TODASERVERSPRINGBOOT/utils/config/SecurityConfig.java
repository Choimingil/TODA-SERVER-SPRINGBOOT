package com.toda.api.TODASERVERSPRINGBOOT.utils.config;

import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.JwtAccessDeniedHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.JwtAuthenticationEntryPoint;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 활성화 (Web)
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // Spring Security should completely ignore URLs starting with /resources/
                .requestMatchers("/resources/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                 토큰을 사용하기 때문에 csrf 설정 disable
                .csrf(AbstractHttpConfigurer::disable)

//                 예외 처리 시 직접 만들었던 클래스 추가
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(JwtAuthenticationEntryPoint.getInstance())
                        .accessDeniedHandler(JwtAccessDeniedHandler.getInstance())
                )

//                 세션 사용하지 않기 때문에 세션 설정 STATELESS
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

//                 토큰이 없는 상태에서 요청이 들어오는 API들은 permitAll
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/email/valid").permitAll()
                        .anyRequest().authenticated()
                )

//                 필터 추가
                .apply(FilterConfig.getInstance());

        return http.build();
    }
}