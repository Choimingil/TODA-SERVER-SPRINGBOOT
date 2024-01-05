package com.toda.api.TODASERVERSPRINGBOOT.config;

import com.toda.api.TODASERVERSPRINGBOOT.interceptors.FcmTokenRedisInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.MdcInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.UserRedisInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final FcmTokenRedisInterceptor fcmTokenRedisInterceptor;
    private final MdcInterceptor mdcInterceptor;
    private final UserRedisInterceptor userRedisInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 먼저 실행되는 순서
        registry.addInterceptor(userRedisInterceptor);
        registry.addInterceptor(fcmTokenRedisInterceptor);
        registry.addInterceptor(mdcInterceptor);
    }
}
