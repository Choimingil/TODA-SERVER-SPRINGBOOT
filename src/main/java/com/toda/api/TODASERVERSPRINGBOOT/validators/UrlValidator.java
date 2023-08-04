package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidUrl;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    @Override
    public void initialize(ValidUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.equals(TokenProvider.SKIP_VALUE)) return true;
        if(value.length()<8) return false;
        String protocol = value.substring(0,8);
        return protocol.equals("https://");
    }
}
