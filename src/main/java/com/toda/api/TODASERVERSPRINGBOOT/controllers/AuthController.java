package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.DecodeTokenResponseDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.CheckToken;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.LoginRequest;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.MdcProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
//public final class AuthController extends AbstractController implements BaseController {
public class AuthController extends AbstractController implements BaseController {
    private final AuthService authService;
    private final MdcProvider mdcProvider;

    //1. 자체 로그인 API
    @PostMapping("/login")
    @SetMdcBody
    public HashMap<String,?> createJwt(
            @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult
    ) {
        String jwt = authService.createJwt(loginRequest);
        SuccessResponse response = new SuccessResponse.Builder(100,"성공적으로 로그인되었습니다.")
                .add("result",jwt)
                .build();
        return response.info;
    }

    //1-3. 토큰 데이터 추출 API
    @GetMapping("/token")
    public HashMap<String,?> decodeToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ) {
        DecodeTokenResponseDto checkTokenResult = authService.decodeToken(token);
        SuccessResponse response = new SuccessResponse.Builder(100,"자체 로그인 성공")
                .add("id",checkTokenResult.getId())
                .add("pw",checkTokenResult.getPw())
                .add("appPw",checkTokenResult.getAppPw())
                .build();
        return response.info;
    }

    //1-4. 토큰 암호 유효성 검사 API
    @PostMapping("/token")
    @SetMdcBody
    public HashMap<String,?> checkToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Nullable CheckToken checkToken,
            BindingResult bindingResult
    ) {
        DecodeTokenResponseDto checkTokenResult = authService.decodeToken(token);
        if(checkToken == null){
            if(checkTokenResult.getAppPw() == 10000){
                SuccessResponse response = new SuccessResponse.Builder(100,"유효한 유저입니다.").build();
                return response.info;
            }
            else throw new ValidationException(404,"앱 비밀번호가 잘못됐습니다.");
        }
        else{
            int appPw = Integer.parseInt(checkToken.getAppPW());
            if(checkTokenResult.getAppPw() == appPw){
                SuccessResponse response = new SuccessResponse.Builder(100,"유효한 유저입니다.").build();
                return response.info;
            }
            else throw new ValidationException(404,"앱 비밀번호가 잘못됐습니다.");
        }
    }
}
