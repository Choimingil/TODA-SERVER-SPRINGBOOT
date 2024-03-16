package com.fineapple.toda.api.abstracts;

import com.fineapple.toda.api.abstracts.interfaces.BaseAspect;
import com.fineapple.toda.api.exceptions.WrongAccessException;
import com.fineapple.toda.api.exceptions.WrongArgException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractAspect implements BaseAspect {
    protected final Logger logger = LoggerFactory.getLogger(AbstractAspect.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResult(final ProceedingJoinPoint joinPoint, String paramName, Class<T> clazz){
        final String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        final List<Object> args = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));

        return IntStream.range(0, parameterNames.length)
                .filter(i -> parameterNames[i].equals(paramName))
                .mapToObj(i -> {
                    if (clazz.isInstance(args.get(i))) return (T) args.get(i);
                    else throw new WrongArgException(WrongArgException.of.WRONG_TYPE_EXCEPTION);
                })
                .findFirst()
                .orElseThrow(() -> new WrongAccessException(WrongAccessException.of.SET_BODY_TO_MDC_EXCEPTION));
    }
}
