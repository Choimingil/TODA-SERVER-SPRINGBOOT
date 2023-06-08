package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserInfoAllDAO {
    Long userID;
    String userCode;
    String email;
    String password;
    String userName;
    String appPassword;

    // 테스트 시 비교를 위한 코드 추가
    @Override
    public int hashCode() {
        return Integer.parseInt(String.valueOf(this.userID));
    }

    // 테스트 시 assertEquals 비교를 위한 추가
    @Override
    public boolean equals(Object obj) {
        // 비교할 DTO 생성
        UserInfoAllDAO userInfoAllDAO = (UserInfoAllDAO) obj;

        // 내부의 모든 데이터가 같다면 true, 그렇지 않다면 false
        return
                this.userID.equals(userInfoAllDAO.userID) &&
                this.userCode.equals(userInfoAllDAO.userCode) &&
                this.email.equals(userInfoAllDAO.email) &&
                this.password.equals(userInfoAllDAO.password) &&
                this.userName.equals(userInfoAllDAO.userName) &&
                this.appPassword.equals(userInfoAllDAO.appPassword);
    }
}
