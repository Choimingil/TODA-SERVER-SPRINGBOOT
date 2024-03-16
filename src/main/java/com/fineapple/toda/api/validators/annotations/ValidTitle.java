package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.TitleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TitleValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTitle {
    String message() default "Valid Title";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
