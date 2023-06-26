package com.toda.api.TODASERVERSPRINGBOOT.utils.extenders;

import jakarta.servlet.http.HttpServletRequest;

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
     * MDC remove method
     */
    void remove();

    /**
     * MDC print log method
     */
    void log();
}
