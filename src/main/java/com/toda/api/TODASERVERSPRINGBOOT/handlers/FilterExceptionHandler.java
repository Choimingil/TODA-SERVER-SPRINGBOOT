package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.providers.SlackProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public final class FilterExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    private final SlackProvider slackProvider;
    private final ObjectMapper objectMapper;

    public void setErrorResponse(int code, String message, HttpServletResponse response) throws IOException {
        sendResponse(response,code,message);
    }

    public void sendErrorToSlack(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception e
    ) throws IOException {
        slackProvider.sendSlackWithNoMdc(request,e);
        sendResponse(response,999,getErrorMsg(e));
    }

    private void sendResponse(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(getErrorResponse(code,msg));
        response.getWriter().write(jsonResponse);
    }
}
