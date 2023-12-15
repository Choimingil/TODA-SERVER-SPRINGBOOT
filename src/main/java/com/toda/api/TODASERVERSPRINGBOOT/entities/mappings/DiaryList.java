package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;

import java.time.LocalDateTime;

public interface DiaryList {
    UserDiary getUserDiary();
    int getUserNum();
}
