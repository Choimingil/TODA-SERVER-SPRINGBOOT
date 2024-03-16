package com.fineapple.toda.api.controllers;

import com.fineapple.toda.api.abstracts.AbstractController;
import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.abstracts.interfaces.BaseController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController extends AbstractController implements BaseController {
    public AdminController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
    }
    // 1. 환불 API
    // 2. 삭제 일기 복구 API
    // 3. 삭제 일기 조회 API
    // 4. Redis 초기화 API
}
