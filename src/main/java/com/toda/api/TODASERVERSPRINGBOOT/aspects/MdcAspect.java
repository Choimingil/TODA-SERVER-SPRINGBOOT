package com.toda.api.TODASERVERSPRINGBOOT.aspects;

import com.toda.api.TODASERVERSPRINGBOOT.aspects.base.AbstractAspect;
import com.toda.api.TODASERVERSPRINGBOOT.aspects.base.BaseAspect;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.MdcProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Aspect
@Component
@RequiredArgsConstructor
public final class MdcAspect extends AbstractAspect implements BaseAspect {
    private final MdcProvider mdcProvider;

    @Around("@annotation(com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody)")
    public Object aspectParameter(final ProceedingJoinPoint joinPoint) throws Throwable{
        mdcProvider.setBody(getResult(joinPoint,"bindingResult", BindingResult.class));
        return joinPoint.proceed();
    }
}
