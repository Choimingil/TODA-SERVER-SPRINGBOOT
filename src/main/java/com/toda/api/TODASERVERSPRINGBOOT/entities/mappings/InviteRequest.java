package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import com.toda.api.TODASERVERSPRINGBOOT.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;

public interface InviteRequest {
    User getUser();
    UserDiary getUserDiary();
    String getSelfie();
}
