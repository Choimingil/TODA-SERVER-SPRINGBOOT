package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Valid Email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
