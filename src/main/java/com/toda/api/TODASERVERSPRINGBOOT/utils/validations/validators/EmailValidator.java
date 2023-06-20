package com.toda.api.TODASERVERSPRINGBOOT.utils.validations.validators;

import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<ValidEmail,String> {
    private static final Pattern EMAIL = Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return EMAIL.matcher(value).matches();
    }
}
