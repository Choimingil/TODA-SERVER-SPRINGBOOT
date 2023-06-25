package com.toda.api.TODASERVERSPRINGBOOT.validations.validators;

import com.toda.api.TODASERVERSPRINGBOOT.utils.RegularExpression;
import com.toda.api.TODASERVERSPRINGBOOT.validations.annotations.ValidAppPw;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AppPwValidator implements ConstraintValidator<ValidAppPw,String> {
    @Override
    public void initialize(ValidAppPw constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.equals("10000")) return true;
        else return RegularExpression.APP_PW.getPattern().matcher(value).matches();
    }
}
