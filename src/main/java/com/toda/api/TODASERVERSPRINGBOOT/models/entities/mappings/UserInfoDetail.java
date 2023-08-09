package com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings;

import java.time.LocalDateTime;

public interface UserInfoDetail {
    long getUserID();
    String getEmail();
    String getPassword();
    String getUserCode();
    int getAppPassword();
    String getUserName();
    LocalDateTime getCreateAt();
    String getProfile();
}
