package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public interface BaseMdc {
    /**
     * MDC에 읽은 유저 정보 저장
     * @param userData
     */
    void setMdc(UserData userData);

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
