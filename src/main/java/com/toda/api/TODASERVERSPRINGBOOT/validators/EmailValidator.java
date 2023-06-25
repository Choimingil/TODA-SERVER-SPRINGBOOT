package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.utils.RegularExpressions;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class EmailValidator implements ConstraintValidator<ValidEmail,String> {
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return RegularExpressions.EMAIL.getPattern().matcher(value).matches();
    }
}
