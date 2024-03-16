package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.UserCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserCodeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserCode {
    String message() default "Valid UserCode";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
