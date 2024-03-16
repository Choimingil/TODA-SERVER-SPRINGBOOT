package com.fineapple.toda.api.validators;

import com.fineapple.toda.api.validators.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class PasswordValidator implements ConstraintValidator<ValidPassword,String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 비밀번호는 8~20자의 영문 대/소문자, 숫자, 특수문자 중 2종류 이상
        if(value.length()<8 || value.length()>20) return false;

        boolean[] check = new boolean[3];
        for(int i=0;i<value.length();i++){
            char curr = value.charAt(i);
            if(curr-'0'>=0 && curr-'0'<=9) check[0] = true;
            else if(curr-'a'>=0 && curr-'z'<=0) check[1] = true;
            else if(curr-'A'>=0 && curr-'Z'<=0) check[1] = true;
            else check[2] = true;
        }

        int checkNum = 0;
        for(boolean isCheck : check) if(isCheck) checkNum++;
        return checkNum>=2;
    }
}
