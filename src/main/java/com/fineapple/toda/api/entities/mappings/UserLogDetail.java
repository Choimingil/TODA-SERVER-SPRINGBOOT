package com.fineapple.toda.api.entities.mappings;

import com.fineapple.toda.api.entities.UserLog;

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
