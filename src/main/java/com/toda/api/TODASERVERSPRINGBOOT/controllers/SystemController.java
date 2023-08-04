package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.ValidateEmail;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.SystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SystemController extends AbstractController implements BaseController {
    private final SystemService systemService;
    private final TokenProvider tokenProvider;
 
    // 1-2. 이메일 중복 확인 API
    @PostMapping("/email/valid")
    @SetMdcBody
    public Map<String, ?> validateEmail(
            @RequestBody @Valid ValidateEmail validateEmail,
            BindingResult bindingResult
    ) {
        if(systemService.isExistEmail(validateEmail.getEmail()))
            return new SuccessResponse.Builder(SuccessResponse.of.VALIDATE_EMAIL_SUCCESS).build().getResponse();
        else return new FailResponse.Builder(FailResponse.of.EXIST_EMAIL_EXCEPTION).build().getResponse();
    }

    // 1-6. 강제 업데이트 API
    @GetMapping("/update")
    public Map<String, ?> checkUpdate(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="type") int type,
            @RequestParam(name="version") String version
    ){
        switch (type) {
            case 1 -> {
                if (systemService.isValidIosVersion(version))
                    return new SuccessResponse.Builder(SuccessResponse.of.CURR_DEVICE_VERSION_SUCCESS).build().getResponse();
                else
                    return new SuccessResponse.Builder(SuccessResponse.of.PREV_DEVICE_VERSION_SUCCESS).build().getResponse();
            }
            case 2 -> {
                if (systemService.isValidAosVersion(version))
                    return new SuccessResponse.Builder(SuccessResponse.of.CURR_DEVICE_VERSION_SUCCESS).build().getResponse();
                else
                    return new SuccessResponse.Builder(SuccessResponse.of.PREV_DEVICE_VERSION_SUCCESS).build().getResponse();
            }
            default -> throw new WrongArgException(WrongArgException.of.WRONG_DEVICE_TYPE_EXCEPTION);
        }
    }

    // 1-11. 자신의 이메일인지 확인 API
    @PostMapping("/email/check")
    public Map<String, ?> checkMyEmail(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid ValidateEmail validateEmail,
            BindingResult bindingResult
    ){
        long userID = tokenProvider.getUserID(token);
        if(systemService.isMyEmail(userID, validateEmail.getEmail()))
            return new SuccessResponse.Builder(SuccessResponse.of.RIGHT_USER_EMAIL_SUCCESS)
                    .add("result",true)
                    .build().getResponse();
        else
            return new SuccessResponse.Builder(SuccessResponse.of.NOT_USER_EMAIL_SUCCESS)
                    .add("result",false)
                    .build().getResponse();
    }

    //1-12. 약관 조회 API
    @GetMapping("/terms")
    public Map<String, ?> getTerms(){
        String term = systemService.readTxtFile("privacy.txt");
        return new SuccessResponse.Builder(SuccessResponse.of.SUCCESS)
                .add("result",term)
                .build().getResponse();
    }
}
