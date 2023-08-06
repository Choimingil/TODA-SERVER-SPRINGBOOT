package com.toda.api.TODASERVERSPRINGBOOT.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

@Component
@RequiredArgsConstructor
public final class DateProvider {
    public String getTimeDiff(LocalDateTime dateTime){
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

    private String toDateString(long val, String unit){
        StringBuilder sb = new StringBuilder();
        sb.append(val).append(unit).append(" 전");
        return sb.toString();
    }
}
