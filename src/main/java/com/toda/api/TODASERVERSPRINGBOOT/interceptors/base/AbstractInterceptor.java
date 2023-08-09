package com.toda.api.TODASERVERSPRINGBOOT.interceptors.base;

import com.toda.api.TODASERVERSPRINGBOOT.enums.LogFields;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractInterceptor implements BaseInterceptor, HandlerInterceptor, InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(AbstractInterceptor.class);
    private final Set<LogFields> logSet = EnumSet.allOf(LogFields.class);
    private final Set<LogFields> mandatoryKeys = EnumSet.of(
            LogFields.REQUEST_ID,
            LogFields.REQUEST_CONTEXT_PATH,
            LogFields.REQUEST_URL,
            LogFields.REQUEST_METHOD,
            LogFields.REQUEST_TIME,
            LogFields.REQUEST_IP
    );

    @Override
    public void afterPropertiesSet() {
        logSet.remove(LogFields.REQUEST_BODY);
    }

    /**
     * 컨트롤러 메서드가 실행되기 전 로직 수행하는 메소드 구현
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     */
    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) {
        return doPreHandleLogic(request,response,handler);
    }

    /**
     * 컨트롤러 메서드 실행 후 로직 수행하는 메소드 구현
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     * (can also be {@code null})
     */
    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView
    ) {
        doPostHandleLogic(request,response,handler);
    }


    /*
     * API 서버이므로 afterCompletion 미구현
     */


    /**
     * 유효한 MDC 로그 키인지 검사
     * @return
     */
    @Override
    public boolean isMdcSet(){
        return mandatoryKeys.stream()
                .allMatch(keys -> keys.get() != null);
    }

    /**
     * MDC에 값 추가
     * @param request
     */
    @Override
    public void setMdc(HttpServletRequest request){
        for(LogFields keys : logSet) keys.add(request,logger);
    }
}
