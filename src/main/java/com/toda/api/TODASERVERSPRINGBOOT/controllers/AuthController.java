package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.models.dto.requests.CheckTokenDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.requests.LoginRequestDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.DecodeTokenResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.DefaultResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.LoginResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.JwtFilter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //1. 자체 로그인 API
    @PostMapping("/login")
    public LoginResponseDTO createJwt(@RequestBody LoginRequestDTO loginRequestDTO) {
        String jwt = authService.createJwt(loginRequestDTO);
        return new LoginResponseDTO(100,"성공적으로 로그인되었습니다.",jwt);
    }

    //1-3. 토큰 데이터 추출 API
    @GetMapping("/token")
    public DecodeTokenResponseDTO decodeToken(@RequestHeader(TokenProvider.HEADER_NAME) String token) {
        return authService.decodeToken(token);
    }

    //1-4. 토큰 암호 유효성 검사 API
    @PostMapping("/token")
    public DefaultResponseDTO checkToken(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody CheckTokenDTO checkTokenDTO
    ) {
        DecodeTokenResponseDTO checkTokenResult = authService.decodeToken(token);
        if(checkTokenResult.isSuccess){
            if(checkTokenDTO == null){
                if(checkTokenResult.appPw == 10000) return new DefaultResponseDTO(100, "유효한 유저입니다.");
                else return new DefaultResponseDTO(404, "앱 비밀번호가 잘못됐습니다.");
            }
            else{
                int appPw = Integer.parseInt(checkTokenDTO.appPW);
                if(checkTokenResult.appPw == appPw) return new DefaultResponseDTO(100, "유효한 유저입니다.");
                else return new DefaultResponseDTO(404, "앱 비밀번호가 잘못됐습니다.");
            }
        }
        else return new DefaultResponseDTO(404, "잘못된 토큰입니다.");
    }


}
