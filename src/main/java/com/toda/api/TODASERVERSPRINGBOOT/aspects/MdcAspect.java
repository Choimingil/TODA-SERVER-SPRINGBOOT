package com.toda.api.TODASERVERSPRINGBOOT.aspects;

import com.toda.api.TODASERVERSPRINGBOOT.aspects.base.AbstractAspect;
import com.toda.api.TODASERVERSPRINGBOOT.aspects.base.BaseAspect;
import com.toda.api.TODASERVERSPRINGBOOT.enums.LogFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
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
    @Around("@annotation(com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody)")
    public Object aspectParameter(final ProceedingJoinPoint joinPoint) throws Throwable{
        setBody(getResult(joinPoint,"bindingResult", BindingResult.class));
        return joinPoint.proceed();
    }

    private void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        LogFields.REQUEST_BODY.add(bindingResult, logger);
    }
}
