package com.toda.api.TODASERVERSPRINGBOOT.annotations;

import com.toda.api.TODASERVERSPRINGBOOT.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Valid Password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
