package com.fineapple.toda.api.entities.mappings;

import com.fineapple.toda.api.entities.User;
import com.fineapple.toda.api.entities.UserDiary;

public interface InviteRequest {
    User getUser();
    UserDiary getUserDiary();
    String getSelfie();
}
