package com.toda.api.TODASERVERSPRINGBOOT.utils.extenders;

import jakarta.servlet.http.HttpServletRequest;

public interface MdcKeysExtender {
    void add(HttpServletRequest request);
    void remove();
    void log();
}
