package com.toda.api.TODASERVERSPRINGBOOT.interceptors.base;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractInterceptor implements BaseInterceptor, HandlerInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(AbstractInterceptor.class);

    /**
     * 컨트롤러 메서드가 실행되기 전 로직 수행하는 메소드 구현
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        boolean isMdcSet = doPreHandleLogic(request,response,handler);
        if(!isMdcSet) throw new ValidationException("MDC_SETTING_EXCEPTION");
        return true;
    }

    /**
     * 컨트롤러 메서드 실행 후 로직 수행하는 메소드 구현
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     * (can also be {@code null})
     * @throws Exception
     */
    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView
    ) throws Exception {
        doPostHandleLogic(request,response,handler);
    }


    /*
     * API 서버이므로 afterCompletion 미구현
     */
}