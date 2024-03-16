package com.fineapple.toda.api.decorators;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public final class CustomTaskDecorator implements TaskDecorator {
    @Override
    public @NotNull Runnable decorate(@NotNull Runnable task) {
        Map<String, String> callerThreadContext = MDC.getCopyOfContextMap();
        return () -> {
            if(callerThreadContext != null) MDC.setContextMap(callerThreadContext);
            task.run();
        };
    }
}
