package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidBinaryChoice;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class BinaryChoiceValidator implements ConstraintValidator<ValidBinaryChoice,String> {
    @Override
    public void initialize(ValidBinaryChoice constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("Y") || value.equals("N");
    }
}
