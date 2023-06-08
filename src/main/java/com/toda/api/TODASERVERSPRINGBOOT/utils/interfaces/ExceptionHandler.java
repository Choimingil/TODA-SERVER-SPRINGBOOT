package com.toda.api.TODASERVERSPRINGBOOT.utils.interfaces;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ExceptionHandler {
    public void setErrorResponse(int code, String message, HttpServletResponse response) throws IOException;
}
