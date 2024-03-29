package com.fineapple.toda.api.validators.annotations;

import com.fineapple.toda.api.validators.MoodValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MoodValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMood {
    String message() default "Valid Mood";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
