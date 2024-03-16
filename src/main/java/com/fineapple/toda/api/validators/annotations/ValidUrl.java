package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.UrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {
    String message() default "Valid Url";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
