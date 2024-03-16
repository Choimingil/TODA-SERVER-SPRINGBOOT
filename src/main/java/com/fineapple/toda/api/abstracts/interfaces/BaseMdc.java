package com.fineapple.toda.api.abstracts.interfaces;

import com.fineapple.toda.api.entities.User;
import jakarta.servlet.http.HttpServletRequest;

public interface BaseMdc {
    /**
     * MDC에 읽은 유저 정보 저장
     * @param user
     * @param profile
     */
    void setMdc(User user, String profile);

    /**
     * MDC에 저장한 유저 정보 삭제
     */
    void removeMdc();

    /**
     * 유효한 MDC 로그 키인지 검사
     * @return
     */
    boolean isMdcSet();

    /**
     * MDC에 값 추가
     * @param request
     */
    void setLogSet(HttpServletRequest request);
}
