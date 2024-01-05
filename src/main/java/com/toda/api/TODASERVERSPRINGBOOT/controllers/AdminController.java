package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController extends AbstractController implements BaseController {
    public AdminController(DelegateDateTime delegateDateTime, DelegateJwt delegateJwt) {
        super(delegateDateTime, delegateJwt);
    }
    // 1. 환불 API
    // 2. 삭제 일기 복구 API
    // 3. 삭제 일기 조회 API
    // 4. Redis 초기화 API
}
