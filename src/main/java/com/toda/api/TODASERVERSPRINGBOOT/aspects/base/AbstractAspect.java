package com.toda.api.TODASERVERSPRINGBOOT.aspects.base;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractAspect implements BaseAspect{
    protected final Logger logger = LoggerFactory.getLogger(AbstractAspect.class);

    @Override
    public <T> T getResult(final ProceedingJoinPoint joinPoint, String paramName, Class<T> c){
        final String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        final List<Object> args = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        for (int i=0; i<parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                if(c.isInstance(args.get(i))){
                    @SuppressWarnings("unchecked") T res = (T) args.get(i);
                    return res;
                }
                else throw new ValidationException("WRONG_TYPE_EXCEPTION");
            }
        }

        throw new ValidationException("SET_BODY_TO_MDC_EXCEPTION");
    }
}
