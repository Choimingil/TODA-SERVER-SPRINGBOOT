package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.validators.annotations.ValidUrl;
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
        if(value.equals(DelegateJwt.SKIP_VALUE)) return true;
        if(value.length()<8) return false;
        String protocol = value.substring(0,8);
        return protocol.equals("https://");
    }
}
