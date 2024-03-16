package com.fineapple.toda.api.abstracts.interfaces;

import jakarta.servlet.http.HttpServletRequest;

public interface BaseUri {
    /**
     * 유효한 URI 여부 체크
     * @param request
     * @return
     */
    boolean isValidUri(HttpServletRequest request);

    /**
     * 토큰이 필요 없는 API 체크
     * @param request
     * @return
     */
    boolean isValidPass(HttpServletRequest request);
}
