package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.DecodeTokenResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.CheckTokenDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.LoginRequestDTO;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //1. 자체 로그인 API
    @PostMapping("/login")
    public HashMap<String,Object> createJwt(
            @RequestBody LoginRequestDTO loginRequestDTO,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");

        String jwt = authService.createJwt(loginRequestDTO);
        SuccessResponse response = new SuccessResponse.Builder(100,"성공적으로 로그인되었습니다.")
                .add("result",jwt)
                .build();
        return response.info;
    }

    //1-3. 토큰 데이터 추출 API
    @GetMapping("/token")
    public HashMap<String,Object> decodeToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ) {
        DecodeTokenResponseDTO checkTokenResult = authService.decodeToken(token);
        SuccessResponse response = new SuccessResponse.Builder(100,"자체 로그인 성공")
                .add("id",checkTokenResult.id)
                .add("pw",checkTokenResult.pw)
                .add("appPw",checkTokenResult.appPw)
                .build();
        return response.info;
    }

    //1-4. 토큰 암호 유효성 검사 API
    @PostMapping("/token")
    public HashMap<String,Object> checkToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody CheckTokenDTO checkTokenDTO,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");
        
        DecodeTokenResponseDTO checkTokenResult = authService.decodeToken(token);
        if(checkTokenDTO == null){
            if(checkTokenResult.appPw == 10000){
                SuccessResponse response = new SuccessResponse.Builder(100,"유효한 유저입니다.").build();
                return response.info;
            }
            else throw new ValidationException(404,"앱 비밀번호가 잘못됐습니다.");
        }
        else{
            int appPw = Integer.parseInt(checkTokenDTO.appPW);
            if(checkTokenResult.appPw == appPw){
                SuccessResponse response = new SuccessResponse.Builder(100,"유효한 유저입니다.").build();
                return response.info;
            }
            else throw new ValidationException(404,"앱 비밀번호가 잘못됐습니다.");
        }
    }


}
