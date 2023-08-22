package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserLogDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.AuthService;
import com.toda.api.TODASERVERSPRINGBOOT.services.SystemService;
import com.toda.api.TODASERVERSPRINGBOOT.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController extends AbstractController implements BaseController {
    private final UserService userService;
    private final SystemService systemService;
    private final AuthService authService;

    @Value("${toda.url.userImage}")
    private String defaultProfile;

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
        return new SuccessResponse.Builder(SuccessResponse.of.CREATE_USER_SUCCESS)
                .build().getResponse();
    }

    //3. 회원탈퇴 API
    @DeleteMapping("/user")
    public Map<String,?> deleteUser(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ){
        userService.deleteUser(token);
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
        userService.updateName(token, updateName.getName());
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
        userService.updatePassword(token, updatePw.getPw());
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
        if(!updateUser.getName().equals(TokenProvider.SKIP_VALUE)) userService.updateName(token, updateUser.getName());
        if(!updateUser.getImage().equals(TokenProvider.SKIP_VALUE)) userService.updateProfile(token, updateUser.getImage());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_USER_SUCCESS)
                .build().getResponse();
    }

    //6-0. 프로필 사진 삭제 API
    @DeleteMapping("/selfie")
    public Map<String,?> deleteProfile(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ){
        userService.updateProfile(token, defaultProfile);
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_PROFILE_SUCCESS)
                .build().getResponse();
    }

    //7. 회원정보조회 API
    @GetMapping("/user")
    public Map<String,?> getUserInfo(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ){
        UserData userData = userService.getUserInfo(token);

        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("userID",userData.getUserID());
        userInfo.put("userCode",userData.getUserCode());
        userInfo.put("email",userData.getEmail());
        userInfo.put("birth",userData.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userInfo.put("name",userData.getUserName());
        userInfo.put("appPW",userData.getAppPassword());
        userInfo.put("selfie",userData.getProfile());

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userInfo)
                .build().getResponse();
    }

    //7-0. 유저코드를 통한 회원정보 조회 API
    @GetMapping("/usercode/{userCode}/user")
    public Map<String,?> getUserInfoWithUserCode(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("userCode") String userCode
    ){
        UserInfoDetail userData = userService.getUserInfoWithUserCode(userCode);

        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("userID",userData.getUserID());
        userInfo.put("userCode",userData.getUserCode());
        userInfo.put("email",userData.getEmail());
        userInfo.put("birth",userData.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userInfo.put("name",userData.getUserName());
        userInfo.put("appPW",userData.getAppPassword());
        userInfo.put("selfie",userData.getProfile());

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userInfo)
                .build().getResponse();
    }

    //7-1. 유저 보유 스티커 조회 API
    @GetMapping("/user/stickers")
    public Map<String,?> getUserStickers(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        List<UserStickerDetail> userStickers = userService.getUserStickers(token,page);
        List<Map<String,?>> result = userStickers.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ID", element.getUserStickerID());
            map.put("stickerPackID", element.getStickerPackID());
            map.put("miniticon", element.getMiniticon());
            return map;
        }).collect(Collectors.toList());

        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",result)
                .build().getResponse();
    }

    //7-2. 임시 비밀번호 발급
    @PostMapping("/user/searchPW")
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
    public Map<String, ?> setAppPassword(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid AppPassword appPassword,
            BindingResult bindingResult
    ){
        UserData userData = userService.updateAppPassword(token, Integer.parseInt(appPassword.getAppPW()));
        String jwt = authService.createJwt(userData.getEmail(), userData.getPassword());
        return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_APP_PASSWORD_SUCCESS)
                .add("token",jwt)
                .build().getResponse();
    }

    //9. 앱 비밀번호 해제 API
    @DeleteMapping("/lock")
    public Map<String, ?> deleteAppPassword(
            @RequestHeader(TokenProvider.HEADER_NAME) String token
    ){
        UserData userData = userService.updateAppPassword(token, 10000);
        String jwt = authService.createJwt(userData.getEmail(), userData.getPassword());
        return new SuccessResponse.Builder(SuccessResponse.of.DELETE_APP_PASSWORD_SUCCESS)
                .add("token",jwt)
                .build().getResponse();
    }

    //10. 알림 조회 API
    @GetMapping("/log")
    public Map<String,?> getUserLog(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestParam(name="page") int page
    ){
        List<UserLogDetail> userLogs = userService.getUserLog(token,page);
        if(userLogs == null) return new SuccessResponse.Builder(SuccessResponse.of.NO_USER_LOG_SUCCESS).build().getResponse();

        List<Map<String,Object>> userLogDetails = userLogs.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("type", element.getType());
            map.put("ID", element.getID());
            map.put("name", element.getName());
            map.put("selfie", element.getSelfie());
            map.put("date", getTimeDiff(element.getDate()));
            map.put("isReplied", element.getIsReplied());
            return map;
        }).toList();
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",userLogDetails)
                .build().getResponse();
    }
}