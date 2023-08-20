package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidTime;
import com.toda.api.TODASERVERSPRINGBOOT.enums.RegularExpressions;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class TimeValidator implements ConstraintValidator<ValidTime, String> {
    @Override
    public void initialize(ValidTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return RegularExpressions.TIME.getPattern().matcher(value).matches();
    }
}
