package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.ValidateEmail;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.AppPassword;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.LoginRequest;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.LoginResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.TokenResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.services.SystemService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController extends AbstractController implements BaseController {
    private final AuthService authService;
    private final SystemService systemService;

    public AuthController(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateUserAuth delegateUserAuth,
            AuthService authService,
            SystemService systemService
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.authService = authService;
        this.systemService = systemService;
    }

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

    //1-1. 자체 로그인 API Ver2
    @PostMapping("/login/ver2")
    @SetMdcBody
    public Map<String,?> createJwtVer2(
            @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult
    ) {
        String jwt = authService.createJwt(loginRequest.getId(), loginRequest.getPw());
        LoginResponse loginResponse = LoginResponse.builder()
                .jwt(jwt)
                .isUpdating(false)
                .build();
        return new SuccessResponse.Builder(SuccessResponse.of.LOGIN_SUCCESS)
                .add("result",loginResponse)
                .build().getResponse();
    }

    //1-3. 토큰 데이터 추출 API
    @GetMapping("/token")
    public Map<String,?> decodeTokenData(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ) {
        Map<String,?> checkTokenResult = authService.getTokenData(token);
        return new SuccessResponse.Builder(SuccessResponse.of.DECODE_TOKEN_SUCCESS)
                .add("id",checkTokenResult.get("id"))
                .add("pw",checkTokenResult.get("pw"))
                .add("appPw",checkTokenResult.get("appPw"))
                .build().getResponse();
    }

    //1-4. 토큰 데이터 추출 API Ver2
    @GetMapping("/token/ver2")
    public Map<String,?> decodeTokenDataVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ) {
        UserDetail userDetail = getUserInfo(token);
        TokenResponse tokenResponse = TokenResponse.builder()
                .date(toStringDateFullTime(userDetail.getUser().getCreateAt()))
                .id(userDetail.getUser().getUserID())
                .appPW(userDetail.getUser().getAppPassword())
                .email(userDetail.getUser().getEmail())
                .code(userDetail.getUser().getUserCode())
                .build();
        return new SuccessResponse.Builder(SuccessResponse.of.DECODE_TOKEN_SUCCESS)
                .add("result",tokenResponse)
                .add("appPW",10000)
                .build().getResponse();
    }

    //1-5. 토큰 암호 유효성 검사 API
    @PostMapping("/token")
    @SetMdcBody
    public Map<String,?> checkToken(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Nullable AppPassword appPassword,
            BindingResult bindingResult
    ) {
        Map<String,?> checkTokenResult = authService.getTokenData(token);

        if(appPassword == null){
            if((int) checkTokenResult.get("appPw") == 10000)
                return new SuccessResponse.Builder(SuccessResponse.of.CHECK_TOKEN_SUCCESS).build().getResponse();
            else throw new WrongArgException(WrongArgException.of.WRONG_APP_PASSWORD_EXCEPTION);
        }
        else{
            int appPw = Integer.parseInt(appPassword.getAppPW());
            if((int) checkTokenResult.get("appPw") == 10000 || (int) checkTokenResult.get("appPw") == appPw)
                return new SuccessResponse.Builder(SuccessResponse.of.CHECK_TOKEN_SUCCESS).build().getResponse();
            else throw new WrongArgException(WrongArgException.of.WRONG_APP_PASSWORD_EXCEPTION);
        }
    }

    // 1-10. 유효성 확인 API
    @GetMapping("/validation")
    public Map<String, ?> checkValidation(@RequestParam(name="email") String email){
        if(systemService.isExistEmail(email))
            return new SuccessResponse.Builder(SuccessResponse.of.VALIDATE_EMAIL_SUCCESS).add("result",true).build().getResponse();
        else return new FailResponse.Builder(FailResponse.of.EXIST_EMAIL_EXCEPTION).add("result",false).build().getResponse();
    }
}
