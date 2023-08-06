package com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings;

import java.time.LocalDateTime;

public interface UserLogDetail {
    public long getType();
    public long getID();
    public String getName();
    public String getSelfie();
    public LocalDateTime getDate();
    public boolean getIsReplied();
}
