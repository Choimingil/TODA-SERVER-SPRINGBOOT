package com.fineapple.toda.api.abstracts;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.abstracts.interfaces.BaseController;
import com.fineapple.toda.api.entities.mappings.UserDetail;
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
        String email = delegateJwt.getEmailWithDecodeToken(token);
        UserDetail userDetail = delegateUserAuth.getUserInfo(email);
        return userDetail.getUser().getUserID();
    }
    protected UserDetail getUserInfo(String token){
        return delegateUserAuth.getUserInfo(token);
    }
}
