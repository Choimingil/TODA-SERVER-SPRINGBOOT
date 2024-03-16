package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.BackgroundValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BackgroundValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBackground {
    String message() default "Valid Background";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
