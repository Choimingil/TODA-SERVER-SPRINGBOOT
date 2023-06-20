package com.toda.api.TODASERVERSPRINGBOOT.utils.config;

import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.UriFilter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.JwtFilter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class FilterConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
    // Singleton Pattern
    private static FilterConfig filterConfig = null;
    public static FilterConfig getInstance(){
        if(filterConfig == null){
            filterConfig = new FilterConfig();
        }
        return filterConfig;
    }

    @Override
    public void configure(HttpSecurity http) {
        // addFilterBefore(A,B) : A를 B 실행 이전에 실행, 즉 A를 B보다 먼저 필터링
        http
                .addFilterBefore(JwtFilter.getInstance(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(UriFilter.getInstance(), JwtFilter.class);
    }
}