package com.toda.api.TODASERVERSPRINGBOOT.utils.validations.validators;

import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public final class PasswordValidator implements ConstraintValidator<ValidPassword,String> {
    private final Pattern PASSWORD = Pattern.compile("^.*(?=^.{8,20}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$");
    
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PASSWORD.matcher(value).matches();
    }
}
