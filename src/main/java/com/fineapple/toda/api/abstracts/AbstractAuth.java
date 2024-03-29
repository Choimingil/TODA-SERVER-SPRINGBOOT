package com.fineapple.toda.api.abstracts;

import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.abstracts.delegates.DelegateMdc;
import com.fineapple.toda.api.entities.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class AbstractAuth {
    protected final Logger logger = LoggerFactory.getLogger(AbstractAuth.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;
    private final DelegateMdc delegateMdc;

    protected String getEmailWithDecodeToken(String token) {
        return delegateJwt.getEmailWithDecodeToken(token);
    }

    protected void setMdc(User user, String profile){
        delegateMdc.setMdc(user,profile);
    }

    protected void removeMdc(){
        delegateMdc.removeMdc();
    }
}
