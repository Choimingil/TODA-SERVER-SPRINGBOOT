package com.toda.api.TODASERVERSPRINGBOOT.aspects.base;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

public abstract class AbstractAspect implements BaseAspect{
    protected final Logger logger = LoggerFactory.getLogger(AbstractAspect.class);

    @Override
    public BindingResult getResult(final ProceedingJoinPoint joinPoint, String paramName){
        final String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        final Object[] arguments = joinPoint.getArgs();
        for (int i=0; i<parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                return (BindingResult) arguments[i];
            }
        }

        throw new ValidationException(500,"MDC에 Request_body를 넣는 중 오류가 발생했습니다.");
    }
}
