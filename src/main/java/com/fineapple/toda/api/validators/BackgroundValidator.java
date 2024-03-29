package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidBackground;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class BackgroundValidator implements ConstraintValidator<ValidBackground,Integer> {
    @Override
    public void initialize(ValidBackground constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return !(value > 7 || value < 1);
    }
}
