package com.toda.api.TODASERVERSPRINGBOOT.controllers.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class AbstractController implements BaseController {
    protected final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    /**
     * LocalDateTime을 00시간 전 형태로 변경 메서드
     * @param dateTime
     * @return
     */
    protected String getTimeDiff(LocalDateTime dateTime){
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(dateTime, now);
        long value = diff.toSeconds();
        if(value<60) return toDateString(value,"초");
        else if(value<60*60) return toDateString(value/60,"분");
        else if(value<60*60*24) return toDateString(value/(60*60),"시간");
        else if(value<60*60*24*30) return toDateString(value/(60*60*24),"일");
        else if(value<60*60*24*30*12) return toDateString(value/(60*60*24*30),"달");
        else return toDateString(value/(60*60*24*30*12),"년");
    }

    /**
     * 날짜 변경한 후 후미에 추가되는 문자열 추가
     * @param val
     * @param unit
     * @return
     */
    private String toDateString(long val, String unit){
        StringBuilder sb = new StringBuilder();
        sb.append(val).append(unit).append(" 전");
        return sb.toString();
    }
}
