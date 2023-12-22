package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidUserName;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class UserNameValidator implements ConstraintValidator<ValidUserName, String> {
    @Override
    public void initialize(ValidUserName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.equals(TokenProvider.SKIP_VALUE)) return true;
        return value.length() < 45;
    }
}
