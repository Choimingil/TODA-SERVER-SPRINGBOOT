package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;

import java.time.LocalDateTime;

public interface UserLogDetail {
//    long getType();
//    long getID();
//    String getName();
//    String getSelfie();
//    LocalDateTime getDate();
//    boolean getIsReplied();

    UserLog getUserLog();
    String getSelfie();
    String getImage();
    boolean getIsReplied();
}
