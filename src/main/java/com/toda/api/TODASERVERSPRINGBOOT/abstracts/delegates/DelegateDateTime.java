package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public final class DelegateDateTime implements BaseDateTime {

    @Override
    public String getDateString(LocalDateTime targetDateTime) {
        long diffSec = getTimeDiffSec(LocalDateTime.now(),targetDateTime);
        StringBuilder sb = new StringBuilder();
        if(diffSec > 60*60*24*30*12) return sb.append(diffSec/(60*60*24*30*12)).append("년 전").toString();
        else if(diffSec > 60*60*24*30) return sb.append(diffSec/(60*60*24*30)).append("달 전").toString();
        else if(diffSec > 60*60*24) return sb.append(diffSec/(60*60*24)).append("일 전").toString();
        else if(diffSec > 60*60) return sb.append(diffSec/(60*60)).append("시간 전").toString();
        else if(diffSec > 60) return sb.append(diffSec/60).append("분 전").toString();
        else return sb.append(diffSec).append("초 전").toString();
    }

    @Override
    public String toStringDateFullTime(LocalDateTime dateTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(dateTime.getYear()).append("-");

        if(dateTime.getMonthValue()<10) sb.append("0");
        sb.append(dateTime.getMonthValue()).append("-");

        if(dateTime.getDayOfMonth()<10) sb.append("0");
        sb.append(dateTime.getDayOfMonth()).append(" ");

        if(dateTime.getHour()<10) sb.append("0");
        sb.append(dateTime.getHour()).append(":");

        if(dateTime.getMinute()<10) sb.append("0");
        sb.append(dateTime.getMinute()).append(":");

        if(dateTime.getSecond()<10) sb.append("0");
        sb.append(dateTime.getSecond());

        return sb.toString();
    }

    @Override
    public LocalDateTime toLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localdate = LocalDate.parse(date, formatter);
        return LocalDateTime.of(localdate, LocalDateTime.now().toLocalTime());
    }

    @Override
    public LocalDateTime toLocalDateTimeFull(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate localdate = LocalDate.parse(date, formatter);
        return LocalDateTime.of(localdate, LocalDateTime.now().toLocalTime());
    }

    @Override
    public long getTimeDiffSec(LocalDateTime currentDateTime, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(targetDateTime, currentDateTime);
        return duration.getSeconds();
    }
}
