package com.toda.api.TODASERVERSPRINGBOOT.config;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.filters.JwtFilter;
import com.toda.api.TODASERVERSPRINGBOOT.filters.UriFilter;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.JwtAccessDeniedHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
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
                .requestMatchers("/uploads/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
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
                            .requestMatchers("/login/ver2").permitAll()
                            .requestMatchers("/email/valid").permitAll()
                            .requestMatchers("/terms").permitAll()
                            .requestMatchers("/user").permitAll()
                            .requestMatchers("/user/ver2").permitAll()
                            .requestMatchers("/user/searchPW").permitAll()
                            .requestMatchers("/update").permitAll()
                            .anyRequest().authenticated()
                    )

//                 필터 추가
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(uriFilter, JwtFilter.class);

            return http.build();
        }
        catch(Exception e){
            throw new RuntimeException();
        }

    }
}