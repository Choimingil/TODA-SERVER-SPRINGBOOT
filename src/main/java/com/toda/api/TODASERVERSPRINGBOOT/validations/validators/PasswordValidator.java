package com.toda.api.TODASERVERSPRINGBOOT.validations.validators;

import com.toda.api.TODASERVERSPRINGBOOT.providers.MdcProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.RegularExpression;
import com.toda.api.TODASERVERSPRINGBOOT.validations.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class PasswordValidator implements ConstraintValidator<ValidPassword,String> {
    private final MdcProvider mdcProvider;
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return RegularExpression.PASSWORD.getPattern().matcher(value).matches();
    }
}
