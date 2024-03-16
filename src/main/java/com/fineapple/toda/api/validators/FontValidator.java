package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidFont;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class FontValidator implements ConstraintValidator<ValidFont,Integer> {
    @Override
    public void initialize(ValidFont constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return !(value > 8 || value < 1);
    }
}
