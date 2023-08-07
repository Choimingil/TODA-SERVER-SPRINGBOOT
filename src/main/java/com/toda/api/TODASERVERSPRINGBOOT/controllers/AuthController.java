package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.GetAppPassword;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.LoginRequest;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController extends AbstractController implements BaseController {
    private final AuthService authService;

    //1. 자체 로그인 API
    @PostMapping("/login")
    @SetMdcBody
    public Map<String,?> createJwt(
            @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult
    ) {
        String jwt = authService.createJwt(loginRequest.getId(), loginRequest.getPw());
        return new SuccessResponse.Builder(SuccessResponse.of.LOGIN_SUCCESS)
                .add("result",jwt)
                .add("isUpdating",false)
                .build().getResponse();
    }

    //1-3. 토큰 데이터 추출 API
    @GetMapping("/token")
    public Map<String,?> decodeToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ) {
        Map<String,?> checkTokenResult = authService.decodeToken(token);
        if(isFail(checkTokenResult)) return checkTokenResult;

        return new SuccessResponse.Builder(SuccessResponse.of.DECODE_TOKEN_SUCCESS)
                .add("id",checkTokenResult.get("id"))
                .add("pw",checkTokenResult.get("pw"))
                .add("appPw",checkTokenResult.get("appPw"))
                .build().getResponse();
    }

    //1-4. 토큰 암호 유효성 검사 API
    @PostMapping("/token")
    @SetMdcBody
    public Map<String,?> checkToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Nullable GetAppPassword appPassword,
            BindingResult bindingResult
    ) {
        Map<String,?> checkTokenResult = authService.decodeToken(token);
        if(isFail(checkTokenResult)) return checkTokenResult;
        if(appPassword == null){
            if((int) checkTokenResult.get("appPw") == 10000)
                return new SuccessResponse.Builder(SuccessResponse.of.CHECK_TOKEN_SUCCESS).build().getResponse();
            else throw new WrongArgException(WrongArgException.of.WRONG_APP_PASSWORD_EXCEPTION);
        }
        else{
            int appPw = Integer.parseInt(appPassword.getAppPW());
            if((int) checkTokenResult.get("appPw") == appPw)
                return new SuccessResponse.Builder(SuccessResponse.of.CHECK_TOKEN_SUCCESS).build().getResponse();
            else throw new WrongArgException(WrongArgException.of.WRONG_APP_PASSWORD_EXCEPTION);
        }
    }
}
