package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController extends AbstractUtil implements BaseController{
    protected final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    /* Delegate Class */
    private final DelegateJwt delegateJwt;
    private final DelegateUserAuth delegateUserAuth;

    public AbstractController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth) {
        super(delegateDateTime, delegateFile, delegateStatus);
        this.delegateJwt = delegateJwt;
        this.delegateUserAuth = delegateUserAuth;
    }
    protected long getUserID(String token) {
        return delegateJwt.getUserID(token);
    }
    protected UserDetail getUserInfo(String token){
        return delegateUserAuth.getUserInfo(token);
    }
//    protected UserDetail decodeToken(String token) {
//        return delegateJwt.decodeToken(token);
//    }

}
