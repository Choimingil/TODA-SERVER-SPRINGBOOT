package com.toda.api.TODASERVERSPRINGBOOT.utils.decorators;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class CustomTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable task) {
        Map<String, String> callerThreadContext = MDC.getCopyOfContextMap();
        return () -> {
            if(callerThreadContext != null) MDC.setContextMap(callerThreadContext);
            task.run();
        };
    }
}
