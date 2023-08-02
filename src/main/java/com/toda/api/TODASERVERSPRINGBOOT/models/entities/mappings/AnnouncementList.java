package com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings;

import java.time.LocalDateTime;

public interface AnnouncementList {
    Long getAnnouncementID();
    String getTitle();
    LocalDateTime getCreateAt();
}
