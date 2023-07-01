package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.DecodeTokenResponseDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.CheckToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.LoginRequest;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Success;
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
        Map<String,?> createJwtResult = authService.createJwt(loginRequest);
        if(isFail(createJwtResult)) return createJwtResult;

        return new SuccessResponse.Builder(Success.LOGIN_SUCCESS)
                .add("result",createJwtResult.get("token"))
                .build().getResponse();
    }

    //1-3. 토큰 데이터 추출 API
    @GetMapping("/token")
    public Map<String,?> decodeToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ) {
        Map<String,?> checkTokenResult = authService.decodeToken(token);
        if(isFail(checkTokenResult)) return checkTokenResult;

        return new SuccessResponse.Builder(Success.DECODE_TOKEN_SUCCESS)
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
            @RequestBody @Nullable CheckToken checkToken,
            BindingResult bindingResult
    ) {
        Map<String,?> checkTokenResult = authService.decodeToken(token);
        if(isFail(checkTokenResult)) return checkTokenResult;
        if(checkToken == null){
            if((int) checkTokenResult.get("appPw") == 10000)
                return new SuccessResponse.Builder(Success.CHECK_TOKEN_SUCCESS).build().getResponse();
            else throw new ValidationException("WRONG_APP_PASSWORD_EXCEPTION");
        }
        else{
            int appPw = Integer.parseInt(checkToken.getAppPW());
            if((int) checkTokenResult.get("appPw") == appPw)
                return new SuccessResponse.Builder(Success.CHECK_TOKEN_SUCCESS).build().getResponse();
            else throw new ValidationException("WRONG_APP_PASSWORD_EXCEPTION");
        }
    }
}
