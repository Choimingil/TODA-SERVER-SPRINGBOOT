package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.enums.RegularExpressions;
import com.fineapple.toda.api.validators.annotations.ValidUserCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class UserCodeValidator implements ConstraintValidator<ValidUserCode, String> {
    @Override
    public void initialize(ValidUserCode constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return RegularExpressions.USER_CODE.getPattern().matcher(value).matches();
    }
}
