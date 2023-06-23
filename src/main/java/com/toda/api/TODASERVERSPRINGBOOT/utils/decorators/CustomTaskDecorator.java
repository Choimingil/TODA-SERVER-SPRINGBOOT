package com.toda.api.TODASERVERSPRINGBOOT.utils.decorators;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public final class CustomTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable task) {
        Map<String, String> callerThreadContext = MDC.getCopyOfContextMap();
        return () -> {
            if(callerThreadContext != null) MDC.setContextMap(callerThreadContext);
            task.run();
        };
    }
}
