package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.SlackProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;
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

    public void setErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        sendResponse(response,code,message);
    }

    public void setErrorResponse(Exceptions exceptions, HttpServletResponse response) throws IOException {
        sendResponse(response,exceptions.code(),exceptions.message());
    }

    public void sendErrorToSlack(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception e
    ) throws IOException {
        slackProvider.doSlack(request,e);
        sendResponse(response,999,getErrorMsg(e));
    }

    private void sendResponse(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(
                new ErrorResponse.Builder(code,msg).build().getResponse()
        );
        response.getWriter().write(jsonResponse);
    }
}
