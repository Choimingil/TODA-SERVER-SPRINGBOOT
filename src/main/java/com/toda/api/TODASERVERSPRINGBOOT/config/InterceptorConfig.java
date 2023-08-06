package com.toda.api.TODASERVERSPRINGBOOT.config;

import com.toda.api.TODASERVERSPRINGBOOT.interceptors.MdcInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.StickerInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final StickerInterceptor stickerInterceptor;
    private final MdcInterceptor mdcInterceptor;
    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 먼저 실행되는 순서
        registry.addInterceptor(tokenInterceptor);
        registry.addInterceptor(mdcInterceptor);
        registry.addInterceptor(stickerInterceptor);
    }
}
