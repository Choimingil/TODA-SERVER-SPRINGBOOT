package com.fineapple.toda.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {
    @Bean
    public LocalValidatorFactoryBean validatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
}
