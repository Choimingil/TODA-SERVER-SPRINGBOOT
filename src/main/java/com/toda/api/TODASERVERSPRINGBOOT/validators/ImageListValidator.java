package com.toda.api.TODASERVERSPRINGBOOT.validators;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidImageList;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public final class ImageListValidator implements ConstraintValidator<ValidImageList,List<String>> {
    @Override
    public void initialize(ValidImageList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> list, ConstraintValidatorContext constraintValidatorContext) {
        for(String value : list){
            if(value.equals(TokenProvider.SKIP_VALUE)) continue;
            if(value.length()<8) return false;
            String protocol = value.substring(0,8);
            return protocol.equals("https://");
        }
        return true;
    }
}
