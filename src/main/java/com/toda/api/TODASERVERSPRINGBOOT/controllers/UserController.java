package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserLogDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmByDevice;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.LoginResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.UserLogResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.services.NotificationService;
import com.toda.api.TODASERVERSPRINGBOOT.services.SystemService;
import com.toda.api.TODASERVERSPRINGBOOT.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController extends AbstractController implements BaseController {
    private final UserService userService;
    private final SystemService systemService;
    private final AuthService authService;
    private final NotificationService notificationService;

    @Value("${toda.url.userImage}")
    private String defaultProfile;

    public UserController(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateUserAuth delegateUserAuth,
            UserService userService,
            SystemService systemService,
            AuthService authService,
            NotificationService notificationService
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.userService = userService;
        this.systemService = systemService;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    //2. 자체 회원가입 API
    @PostMapping("/user")
    @SetMdcBody
    public Map<String,?> createUser(
            @RequestBody @Valid CreateUser createUser,
            BindingResult bindingResult
    ) {
        if(!systemService.isExistEmail(createUser.getEmail()))
            return new FailResponse.Builder(FailResponse.of.EXIST_EMAIL_EXCEPTION).build().getResponse();

        long userID = userService.createUser(createUser);
        userService.createUserImage(userID,defaultProfile);
        userService.setUserSticker(userID, null);
        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_USER_SUCCESS).build().getResponse();
    }

    //2-1. 자체 회원가입 API Ver2
    @PostMapping("/user/ver2")
    @SetMdcBody
    public Map<String,?> createUserVer2(
            @RequestBody @Valid CreateUser createUser,
            BindingResult bindingResult
    ) {
        if(!systemService.isExistEmail(createUser.getEmail()))
            return new FailResponse.Builder(FailResponse.of.EXIST_EMAIL_EXCEPTION).build().getResponse();

        long userID = userService.createUser(createUser);
        userService.createUserImage(userID,defaultProfile);
        userService.setUserSticker(userID, null);
        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_USER_SUCCESS).add("result",userID).build().getResponse();
    }

    //3. 회원탈퇴 API
    @DeleteMapping("/user")
    public Map<String,?> deleteUser(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ){
        userService.deleteUser(token);
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_USER_SUCCESS).build().getResponse();
    }

    //4. 닉네임 변경 API
    @PatchMapping("/name")
    @SetMdcBody
    public Map<String,?> updateName(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdateName updateName,
            BindingResult bindingResult
    ){
        userService.updateName(token, updateName.getName());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_NAME_SUCCESS).build().getResponse();
    }

    //5. 비밀번호 변경 API
    @PatchMapping("/password")
    @SetMdcBody
    public Map<String,?> updatePw(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdatePw updatePw,
            BindingResult bindingResult
    ){
        userService.updatePassword(token, updatePw.getPw());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_PASSWORD_SUCCESS)
                .build().getResponse();
    }

    //5-1. 비밀번호 변경 API Ver2
    @PatchMapping("/password/ver2")
    @SetMdcBody
    public Map<String,?> updatePwVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdatePw updatePw,
            BindingResult bindingResult
    ){
        User user = userService.updatePassword(token, updatePw.getPw());
        String jwt = authService.createJwt(user.getEmail(), user.getPassword());
        LoginResponse loginResponse = LoginResponse.builder()
                .jwt(jwt)
                .isUpdating(false)
                .build();
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_PASSWORD_SUCCESS)
                .add("result",loginResponse)
                .build().getResponse();
    }

    //6. 유저 정보 변경 API : 닉네임과 프로필 둘 다 변경
    @PatchMapping("/user")
    @SetMdcBody
    public Map<String,?> updateUser(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody UpdateUser updateUser,
            BindingResult bindingResult
    ){
        if(updateUser.getName()!=null && !updateUser.getName().equals(DelegateJwt.SKIP_VALUE)) userService.updateName(token, updateUser.getName());
        if(updateUser.getImage()!=null && !updateUser.getImage().equals(DelegateJwt.SKIP_VALUE)) userService.updateProfile(token, updateUser.getImage());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_USER_SUCCESS).build().getResponse();
    }

    //6-0. 프로필 사진 삭제 API
    @DeleteMapping("/selfie")
    public Map<String,?> deleteProfile(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ){
        userService.updateProfile(token, defaultProfile);
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_PROFILE_SUCCESS)
                .build().getResponse();
    }

    //7. 회원정보조회 API
    @GetMapping("/user")
    public Map<String,?> getUserDataInfo(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ){
        UserDetail userDetail = getUserInfo(token);

        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("userID",userDetail.getUser().getUserID());
        userInfo.put("userCode",userDetail.getUser().getUserCode());
        userInfo.put("email",userDetail.getUser().getEmail());
        userInfo.put("birth",userDetail.getUser().getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userInfo.put("name",userDetail.getUser().getUserName());
        userInfo.put("appPW",userDetail.getUser().getAppPassword());
        userInfo.put("selfie",userDetail.getProfile());

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userInfo)
                .build().getResponse();
    }

    //7-0. 유저코드를 통한 회원정보 조회 API
    @GetMapping("/usercode/{userCode}/user")
    public Map<String,?> getUserInfoWithUserCode(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("userCode") String userCode
    ){
        UserDetail userDetail = userService.getUserInfoWithUserCode(userCode);

        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("userID",userDetail.getUser().getUserID());
        userInfo.put("userCode",userDetail.getUser().getUserCode());
        userInfo.put("email",userDetail.getUser().getEmail());
        userInfo.put("birth",userDetail.getUser().getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userInfo.put("name",userDetail.getUser().getUserName());
        userInfo.put("appPW",userDetail.getUser().getAppPassword());
        userInfo.put("selfie",userDetail.getProfile());

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userInfo)
                .build().getResponse();
    }

    //7-1. 유저코드를 통한 회원정보 조회 API Ver2
    @GetMapping("/usercode/{userCode}/user/ver2")
    public Map<String,?> getUserInfoWithUserCodeVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("userCode") String userCode
    ){
        UserDetail userDetail = userService.getUserInfoWithUserCode(userCode);
        List<FcmByDevice> fcmByDeviceList = notificationService.getFcmByDevice(userDetail.getUser().getUserID());

        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("userID",userDetail.getUser().getUserID());
        userInfo.put("userCode",userDetail.getUser().getUserCode());
        userInfo.put("email",userDetail.getUser().getEmail());
        userInfo.put("name",userDetail.getUser().getUserName());
        userInfo.put("selfie",userDetail.getProfile());
        userInfo.put("token",fcmByDeviceList);

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userInfo)
                .build().getResponse();
    }

    //7-3. 임시 비밀번호 발급 API
    @PostMapping("/user/searchPW")
    @SetMdcBody
    public Map<String, ?> getTempPassword(
            @RequestBody @Valid GetTempPw getTempPw,
            BindingResult bindingResult
    ){
        userService.updateTempPassword(getTempPw.getId());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_TEMP_PASSWORD_SUCCESS)
                .build().getResponse();
    }

    //8. 앱 비밀번호 설정 API
    @PostMapping("/lock")
    @SetMdcBody
    public Map<String, ?> setAppPassword(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid AppPassword appPassword,
            BindingResult bindingResult
    ){
        UserDetail userDetail = userService.updateAppPassword(token, Integer.parseInt(appPassword.getAppPW()));
        String jwt = authService.createJwt(userDetail.getUser().getEmail(), userDetail.getUser().getPassword());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_APP_PASSWORD_SUCCESS)
                .add("token",jwt)
                .build().getResponse();
    }

    //8-1. 앱 비밀번호 설정 API Ver2
    @PostMapping("/lock/ver2")
    @SetMdcBody
    public Map<String, ?> setAppPasswordVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid AppPassword appPassword,
            BindingResult bindingResult
    ){
        UserDetail userDetail = userService.updateAppPassword(token, Integer.parseInt(appPassword.getAppPW()));
        String jwt = authService.createJwt(userDetail.getUser().getEmail(), userDetail.getUser().getPassword());
        LoginResponse loginResponse = LoginResponse.builder()
                .jwt(jwt)
                .isUpdating(false)
                .build();
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_APP_PASSWORD_SUCCESS)
                .add("result",loginResponse)
                .build().getResponse();
    }

    //9. 앱 비밀번호 해제 API
    @DeleteMapping("/lock")
    public Map<String, ?> deleteAppPassword(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ){
        UserDetail userDetail = userService.updateAppPassword(token, 10000);
        String jwt = authService.createJwt(userDetail.getUser().getEmail(), userDetail.getUser().getPassword());
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_APP_PASSWORD_SUCCESS)
                .add("token",jwt)
                .build().getResponse();
    }

    //9-1. 앱 비밀번호 해제 API Ver2
    @DeleteMapping("/lock/ver2")
    public Map<String, ?> deleteAppPasswordVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token
    ){
        UserDetail userDetail = userService.updateAppPassword(token, 10000);
        String jwt = authService.createJwt(userDetail.getUser().getEmail(), userDetail.getUser().getPassword());
        LoginResponse loginResponse = LoginResponse.builder()
                .jwt(jwt)
                .isUpdating(false)
                .build();
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_APP_PASSWORD_SUCCESS)
                .add("result",loginResponse)
                .build().getResponse();
    }

    //10. 알림 조회 API
    @GetMapping("/log")
    public Map<String,?> getUserLog(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        long userID = getUserID(token);
        List<UserLogResponse> userLogResponseList = userService.getUserLog(userID,page);
        if(userLogResponseList == null) return new SuccessResponse.Builder(SuccessResponse.of.NO_USER_LOG_SUCCESS).add("result",new ArrayList<>()).build().getResponse();
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userLogResponseList)
                .build().getResponse();
    }
}