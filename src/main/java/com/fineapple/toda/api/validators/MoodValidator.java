package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidMood;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class MoodValidator implements ConstraintValidator<ValidMood,Integer> {
    @Override
    public void initialize(ValidMood constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return !(value > 7 || value < 1) || (value == 999);
//        return !(value > 7 || value < 1);
    }
}
