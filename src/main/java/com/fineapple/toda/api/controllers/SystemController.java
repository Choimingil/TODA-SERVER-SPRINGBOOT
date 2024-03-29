package com.fineapple.toda.api.controllers;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.models.responses.FailResponse;
import com.fineapple.toda.api.exceptions.WrongArgException;
import com.fineapple.toda.api.annotations.SetMdcBody;
import com.fineapple.toda.api.abstracts.AbstractController;
import com.fineapple.toda.api.abstracts.interfaces.BaseController;
import com.fineapple.toda.api.models.responses.SuccessResponse;
import com.fineapple.toda.api.models.bodies.ValidateEmail;
import com.fineapple.toda.api.services.SystemService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class SystemController extends AbstractController implements BaseController {
    private final SystemService systemService;

    public SystemController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, SystemService systemService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.systemService = systemService;
    }

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

    // 1-8. 강제 업데이트 API
    @GetMapping("/update")
    public Map<String, ?> checkUpdate(
            @RequestHeader(value = DelegateJwt.HEADER_NAME, required = false) String token,
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

    // 1-9. 강제 업데이트 API Ver2
    @GetMapping("/update/ver2")
    public Map<String, ?> checkUpdateVer2(
            @RequestHeader(value = DelegateJwt.HEADER_NAME, required = false) String token,
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
    @SetMdcBody
    public Map<String, ?> checkMyEmail(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid ValidateEmail validateEmail,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
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
        String term = systemService.readPrivacyTerm();
        return new SuccessResponse.Builder(SuccessResponse.of.SUCCESS)
                .add("result",term)
                .build().getResponse();
    }
}
