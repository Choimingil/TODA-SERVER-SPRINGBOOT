package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidUserCode;
import com.toda.api.TODASERVERSPRINGBOOT.enums.RegularExpressions;
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
