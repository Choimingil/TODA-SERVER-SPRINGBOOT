package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import org.aspectj.lang.ProceedingJoinPoint;

public interface BaseAspect {
    <T> T getResult(final ProceedingJoinPoint joinPoint, String paramName, Class<T> c);
}
