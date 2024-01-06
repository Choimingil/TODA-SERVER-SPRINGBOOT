package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateFile;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateStatus;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractController extends AbstractUtil implements BaseController{
    protected final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;

    public AbstractController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt) {
        super(delegateDateTime, delegateFile, delegateStatus);
        this.delegateJwt = delegateJwt;
    }
    protected long getUserID(String token) {
        return delegateJwt.getUserID(token);
    }
    protected UserData decodeToken(String token) {
        return delegateJwt.decodeToken(token);
    }

}
