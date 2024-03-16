package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.TimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTime {
    String message() default "Valid Time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
