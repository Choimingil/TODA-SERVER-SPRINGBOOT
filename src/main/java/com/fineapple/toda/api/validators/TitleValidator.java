package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidTitle;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class TitleValidator implements ConstraintValidator<ValidTitle, String> {

    @Override
    public void initialize(ValidTitle constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length()<45;
    }
}
