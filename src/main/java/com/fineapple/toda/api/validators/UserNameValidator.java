package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidUserName;
import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
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
        if(value.equals(DelegateJwt.SKIP_VALUE)) return true;
        return value.length() < 45;
    }
}
