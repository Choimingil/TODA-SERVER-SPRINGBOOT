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
        /**
         * User
         */
        NO_USER_SELFIE_EXCEPTION(103,"유저 프로필 사진이 존재하지 않습니다"),

        /**
         * Diary
         */
        SELF_INVITE_EXCEPTION(501,"자기 자신을 등록할 수 없습니다."),
        NO_DIARY_EXCEPTION(401,"다이어리에 등록되지 않은 사용자입니다."),
        ALONE_DIARY_INVITATION_EXCEPTION(310, "혼자 쓰는 다이어리에 친구를 초대할 수 없습니다."),
        EXIST_USER_DIARY_EXCEPTION(501,"이미 다이어리에 등록된 사용자입니다."),
        WRONG_DIARY_STATUS_EXCEPTION(103,"잘못된 다이어리 변경입니다."),
        ALREADY_INVITE_EXCEPTION(501,"이미 초대한 사용자입니다."),

        /**
         * Post
         */
        NO_USER_POST_EXCEPTION(102,"자신이 작성한 게시물이 아닙니다."),
        NO_AUTH_POST_EXCEPTION(102,"게시물을 볼 수 있는 권한이 없습니다."),
        WRONG_HEART_STATUS_EXCEPTION(103,"잘못된 좋아요 상태값입니다."),

        /**
         * Comment
         */
        NO_AUTH_COMMENT_EXCEPTION(102,"대댓글을 달 권한이 없습니다."),
        NO_USER_COMMENT_EXCEPTION(102,"자신이 작성한 댓글이 아닙니다."),

        /**
         * Sticker
         */
        NO_AUTH_STICKER_EXCEPTION(102,"보유한 스티커가 아닙니다."),
        NO_USER_STICKER_EXCEPTION(103,"자신이 등록한 스티커가 아닙니다."),
        ;

        private final int code;
        private final String message;
    }

    private final BusinessLogicException.of element;
}
