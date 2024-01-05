package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateMdc;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class AbstractAuth {
    protected final Logger logger = LoggerFactory.getLogger(AbstractAuth.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;
    private final DelegateMdc delegateMdc;

    protected UserData decodeToken(String token) {
        return delegateJwt.decodeToken(token);
    }

    protected void setMdc(UserData userData){
        delegateMdc.setMdc(userData);
    }

    protected void removeMdc(){
        delegateMdc.removeMdc();
    }
}
