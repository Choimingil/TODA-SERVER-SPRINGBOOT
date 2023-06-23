package com.toda.api.TODASERVERSPRINGBOOT.utils.config;

import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.JwtFilter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.MdcFilter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.UriFilter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.JwtAccessDeniedHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.JwtAuthenticationEntryPoint;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Key;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final MdcFilter mdcFilter;
    private final JwtFilter jwtFilter;
    private final UriFilter uriFilter;

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
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
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
                .addFilterAfter(mdcFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(uriFilter, JwtFilter.class);

        return http.build();
    }
}