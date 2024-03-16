package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.UserNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserNameValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserName {
    String message() default "Valid UserName";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
