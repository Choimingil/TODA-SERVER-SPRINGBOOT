package com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings;

import java.time.LocalDateTime;

public interface AnnouncementDetail {
    String getTitle();
    LocalDateTime getCreateAt();
    String getImage();
    String getText();
}
