package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.AppPwValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AppPwValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAppPw {
    String message() default "Valid AppPw";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
