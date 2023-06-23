package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public final class UserController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //2. 자체 회원가입 API
    @PostMapping("/user")
    public String createUser(String str) {
        return str;
    }

    // $r->addRoute('PATCH', '/name', ['UserController', 'updateName']);                                                       //4. 닉네임 변경 API
    // $r->addRoute('PATCH', '/password', ['UserController', 'updatePassword']);
    // $r->addRoute('DELETE', '/selfie', ['UserController', 'deleteSelfie']);                                                  //6-0. 프로필 사진 삭제 API

    // $r->addRoute('GET', '/user', ['UserController', 'getUser']);                                                            //7. 회원정보조회 API


    // $r->addRoute('GET', '/usercode/{userCode}/user', ['UserController', 'getUserByUserCode']);                              //7-0. 유저코드를 통한 회원정보 조회 API
    // $r->addRoute('GET', '/user/stickers', ['StickerController', 'getUserStickers']);                                        //7-1. 유저 보유 스티커 조회 API(스티커 Controller에 존재)
    // $r->addRoute('POST', '/user/searchPW', ['UserController', 'getTmpPw']);                                                  //7-2. 임시 비밀번호 발급

    // $r->addRoute('POST', '/lock', ['LoginController', 'postLock']);
    // $r->addRoute('DELETE', '/lock', ['LoginController', 'deleteLock']);

    // $r->addRoute('GET', '/log', ['UserController', 'getLog']);                                                              //10. 알림 조회 API

}
