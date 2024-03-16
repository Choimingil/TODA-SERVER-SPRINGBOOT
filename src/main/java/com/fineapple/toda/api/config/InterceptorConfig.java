package com.fineapple.toda.api.config;

import com.fineapple.toda.api.interceptors.MdcInterceptor;
import com.fineapple.toda.api.interceptors.UserRedisInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final MdcInterceptor mdcInterceptor;
    private final UserRedisInterceptor userRedisInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 먼저 실행되는 순서
        registry.addInterceptor(userRedisInterceptor);
        registry.addInterceptor(mdcInterceptor);
    }
}
