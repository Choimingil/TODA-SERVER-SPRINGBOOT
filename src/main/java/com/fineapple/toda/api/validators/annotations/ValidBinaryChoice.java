package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.BinaryChoiceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BinaryChoiceValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBinaryChoice {
    String message() default "Valid Binary Choice";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
