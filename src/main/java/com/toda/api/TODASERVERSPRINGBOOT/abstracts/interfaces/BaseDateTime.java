package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import java.time.LocalDateTime;

public interface BaseDateTime {
    /**
     * 현재 시간과 주어진 시간 사이의 차이값을 시간 단위로 변환하여 리턴
     * @param targetDateTime
     * @return
     */
    String getDateString(LocalDateTime targetDateTime);

    /**
     * LocalDateTime값을 yyyy-MM-dd 꼴로 변환
     * @param dateTime
     * @return
     */
    String toStringDateFullTime(LocalDateTime dateTime);

    /**
     * "yyyy-MM-dd" 형식의 날짜 String을 LocalDateTime으로 변환
     * @param date
     * @return
     */
    LocalDateTime toLocalDateTime(String date);

    /**
     * "yyyy-MM-dd HH:mm:ss" 형식의 날짜 String을 LocalDateTime으로 변환
     * @param date
     * @return
     */
    LocalDateTime toLocalDateTimeFull(String date);

    /**
     * 두 시간 사이의 초 시간 리턴
     * @param currentDateTime
     * @param targetDateTime
     * @return
     */
    long getTimeDiffSec(LocalDateTime currentDateTime, LocalDateTime targetDateTime);
}
