package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Valid Date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
