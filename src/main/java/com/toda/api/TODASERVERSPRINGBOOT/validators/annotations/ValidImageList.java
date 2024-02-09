package com.toda.api.TODASERVERSPRINGBOOT.validators.annotations;

import com.toda.api.TODASERVERSPRINGBOOT.validators.ImageListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageListValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageList {
    String message() default "Valid ImageList";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
