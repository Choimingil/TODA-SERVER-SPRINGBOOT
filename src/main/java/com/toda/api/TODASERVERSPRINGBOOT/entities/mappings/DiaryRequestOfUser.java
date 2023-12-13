package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public interface DiaryRequestOfUser {
    long getUserID();
    String getUserCode();
    String getEmail();
    String getUserName();
    String getSelfie();
    long getDiaryID();
    String getDiaryName();
    LocalDateTime getDate();
}
