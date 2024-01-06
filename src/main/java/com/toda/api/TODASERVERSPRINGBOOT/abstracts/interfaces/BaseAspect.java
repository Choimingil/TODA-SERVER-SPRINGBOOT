package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import org.aspectj.lang.ProceedingJoinPoint;

public interface BaseAspect {
    /**
     * 가져온 객체에 특정 파라미터가 존재한다면 그 값을 가져오기
     * @param joinPoint
     * @param paramName
     * @param c
     * @return
     * @param <T>
     */
    <T> T getResult(final ProceedingJoinPoint joinPoint, String paramName, Class<T> c);
}
