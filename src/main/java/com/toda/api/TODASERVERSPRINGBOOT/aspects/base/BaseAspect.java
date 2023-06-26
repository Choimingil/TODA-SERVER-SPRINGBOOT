package com.toda.api.TODASERVERSPRINGBOOT.aspects.base;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.validation.BindingResult;

public interface BaseAspect {
    BindingResult getResult(final ProceedingJoinPoint joinPoint, String paramName);
}
