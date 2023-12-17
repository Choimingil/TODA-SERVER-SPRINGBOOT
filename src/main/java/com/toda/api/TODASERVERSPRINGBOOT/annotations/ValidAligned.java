package com.toda.api.TODASERVERSPRINGBOOT.annotations;

import com.toda.api.TODASERVERSPRINGBOOT.validators.AlignedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AlignedValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAligned {
    String message() default "Valid Background";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
