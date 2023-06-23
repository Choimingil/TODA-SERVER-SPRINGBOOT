package com.toda.api.TODASERVERSPRINGBOOT.utils.validations.validators;

import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidAppPw;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public final class AppPwValidator implements ConstraintValidator<ValidAppPw,String> {
    private final Pattern APP_PW = Pattern.compile("^[0-9]{1,4}$");

    @Override
    public void initialize(ValidAppPw constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.equals("10000")) return true;
        else return APP_PW.matcher(value).matches();
    }
}