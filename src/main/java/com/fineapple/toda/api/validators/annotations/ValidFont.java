package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.FontValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FontValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFont {
    String message() default "Valid Font";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
