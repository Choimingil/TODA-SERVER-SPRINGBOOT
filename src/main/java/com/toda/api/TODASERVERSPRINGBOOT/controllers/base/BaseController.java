package com.toda.api.TODASERVERSPRINGBOOT.controllers.base;

import java.time.LocalDateTime;

public interface BaseController {
    /**
     * LocalDateTime을 00시간 전 형태로 변경 메서드
     * @param dateTime
     * @return
     */
    String getTimeDiff(LocalDateTime dateTime);
}
