package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidAligned;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AlignedValidator implements ConstraintValidator<ValidAligned,Integer> {
    @Override
    public void initialize(ValidAligned constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return !(value > 3 || value < 1);
    }
}
