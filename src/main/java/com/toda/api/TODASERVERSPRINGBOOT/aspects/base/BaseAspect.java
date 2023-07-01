package com.toda.api.TODASERVERSPRINGBOOT.aspects.base;

import org.aspectj.lang.ProceedingJoinPoint;

public interface BaseAspect {
    <T> T getResult(final ProceedingJoinPoint joinPoint, String paramName, Class<T> c);
}
