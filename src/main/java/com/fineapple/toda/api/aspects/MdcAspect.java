package com.fineapple.toda.api.aspects;

import com.fineapple.toda.api.abstracts.AbstractAspect;
import com.fineapple.toda.api.abstracts.interfaces.BaseAspect;
import com.fineapple.toda.api.enums.LogFields;
import com.fineapple.toda.api.exceptions.WrongArgException;
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
    @Around("@annotation(com.fineapple.toda.api.annotations.SetMdcBody)")
    public Object aspectParameter(final ProceedingJoinPoint joinPoint) throws Throwable{
        setBody(getResult(joinPoint,"bindingResult", BindingResult.class));
        return joinPoint.proceed();
    }

    private void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        LogFields.REQUEST_BODY.add(bindingResult, logger);
    }
}
