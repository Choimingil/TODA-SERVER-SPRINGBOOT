package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.enums.RegularExpressions;
import com.fineapple.toda.api.validators.annotations.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DateValidator implements ConstraintValidator<ValidDate,String> {
    @Override
    public void initialize(ValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return RegularExpressions.DATE.getPattern().matcher(value).matches();
    }
}
