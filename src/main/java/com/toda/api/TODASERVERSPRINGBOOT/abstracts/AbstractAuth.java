package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateMdc;
import com.toda.api.TODASERVERSPRINGBOOT.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class AbstractAuth {
    protected final Logger logger = LoggerFactory.getLogger(AbstractAuth.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;
    private final DelegateMdc delegateMdc;

    protected UserDetail decodeToken(String token) {
        return delegateJwt.decodeToken(token);
    }

    protected void setMdc(User user, String profile){
        delegateMdc.setMdc(user,profile);
    }

    protected void removeMdc(){
        delegateMdc.removeMdc();
    }
}
