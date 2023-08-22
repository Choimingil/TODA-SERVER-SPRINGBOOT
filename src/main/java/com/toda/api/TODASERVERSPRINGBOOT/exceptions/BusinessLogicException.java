package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public class BusinessLogicException extends IllegalArgumentException{
    @RequiredArgsConstructor
    @Getter
    public enum of{
        SELF_INVITE_EXCEPTION(501,"자기 자신을 등록할 수 없습니다."),
        NO_DIARY_EXCEPTION(401,"다이어리에 등록되지 않은 사용자입니다."),
        ALONE_DIARY_INVITATION_EXCEPTION(310, "혼자 쓰는 다이어리에 친구를 초대할 수 없습니다."),
        EXIST_USER_DIARY_EXCEPTION(501,"이미 다이어리에 등록된 사용자입니다."),
        ALREADY_INVITE_EXCEPTION(501,"이미 초대한 사용자입니다.");

        private final int code;
        private final String message;
    }

    private final BusinessLogicException.of element;
}
