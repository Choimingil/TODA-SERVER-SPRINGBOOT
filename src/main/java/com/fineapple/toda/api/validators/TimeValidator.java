package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.enums.RegularExpressions;
import com.fineapple.toda.api.validators.annotations.ValidTime;
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
