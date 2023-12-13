package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import java.time.LocalDateTime;

public interface AnnouncementList {
    Long getAnnouncementID();
    String getTitle();
    LocalDateTime getCreateAt();
}
