package com.toda.api.TODASERVERSPRINGBOOT.utils.extenders;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

public interface MdcKeysExtender {
    /**
     * MDC get method
     */
    String get();

    /**
     * MDC put method
     * @param request
     */
    void add(HttpServletRequest request);

    /**
     * MDC put method for body
     * @param bindingResult
     */
    void add(BindingResult bindingResult);

    /**
     * MDC remove method
     */
    void remove();

    /**
     * MDC print log method
     */
    void log();
}
