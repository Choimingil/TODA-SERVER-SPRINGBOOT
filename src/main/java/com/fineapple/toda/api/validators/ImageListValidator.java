package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.validators.annotations.ValidImageList;
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
            if(value.equals(DelegateJwt.SKIP_VALUE)) continue;
            if(value.length()<8) return false;
            String protocol = value.substring(0,8);
            return protocol.equals("https://");
        }
        return true;
    }
}
