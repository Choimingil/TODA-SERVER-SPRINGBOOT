package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.SystemService;
import com.toda.api.TODASERVERSPRINGBOOT.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController extends AbstractController implements BaseController {
    private final UserService userService;
    private final SystemService systemService;
    private final TokenProvider tokenProvider;

    //2. 자체 회원가입 API
    @PostMapping("/user")
    @SetMdcBody
    public Map<String,?> createUser(
            @RequestBody @Valid CreateUser createUser,
            BindingResult bindingResult
    ) {
        if(!systemService.isExistEmail(createUser.getEmail()))
            return new FailResponse.Builder(FailResponse.of.EXIST_EMAIL_EXCEPTION).build().getResponse();

        String code = userService.createUserCode();
        long userID = userService.createUser(createUser,code);
        userService.createUserImage(userID);
        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_USER_SUCCESS)
                .build().getResponse();
    }

    //3. 회원탈퇴 API
    @DeleteMapping("/user")
    public Map<String,?> deleteUser(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ){
        long userID = tokenProvider.getUserID(token);
        userService.deleteUser(userID);
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_USER_SUCCESS)
                .build().getResponse();
    }

    //4. 닉네임 변경 API
    @PatchMapping("/name")
    public Map<String,?> updateName(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateName updateName,
            BindingResult bindingResult
    ){
        long userID = tokenProvider.getUserID(token);
        userService.updateName(userID, updateName.getName());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_NAME_SUCCESS)
                .build().getResponse();
    }

    //5. 비밀번호 변경 API
    @PatchMapping("/password")
    public Map<String,?> updatePw(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdatePw updatePw,
            BindingResult bindingResult
    ){
        long userID = tokenProvider.getUserID(token);
        userService.updatePassword(userID, updatePw.getPw());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_PASSWORD_SUCCESS)
                .build().getResponse();
    }

    //6. 유저 정보 변경 API : 닉네임과 프로필 둘 다 변경
    @PatchMapping("/user")
    public Map<String,?> updateUser(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateUser updateUser,
            BindingResult bindingResult
    ){
        long userID = tokenProvider.getUserID(token);
        if(!updateUser.getName().equals(TokenProvider.SKIP_VALUE)) userService.updateName(userID, updateUser.getName());
        if(!updateUser.getImage().equals(TokenProvider.SKIP_VALUE)) userService.updateProfile(userID, updateUser.getImage());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_USER_SUCCESS)
                .build().getResponse();
    }

    // $r->addRoute('DELETE', '/selfie', ['UserController', 'deleteSelfie']);                                                  //6-0. 프로필 사진 삭제 API
    // $r->addRoute('GET', '/user', ['UserController', 'getUser']);                                                            //7. 회원정보조회 API
    // $r->addRoute('GET', '/usercode/{userCode}/user', ['UserController', 'getUserByUserCode']);                              //7-0. 유저코드를 통한 회원정보 조회 API
    // $r->addRoute('GET', '/log', ['UserController', 'getLog']);                                                              //10. 알림 조회 API



    // SystemController에 추가
    // $r->addRoute('POST', '/user/searchPW', ['UserController', 'getTmpPw']);                                                  //7-2. 임시 비밀번호 발급


    // LockController 만들기
    // Redis 내용도 같이 수정해야 함
    // $r->addRoute('POST', '/lock', ['LoginController', 'postLock']);
    // $r->addRoute('DELETE', '/lock', ['LoginController', 'deleteLock']);
}
