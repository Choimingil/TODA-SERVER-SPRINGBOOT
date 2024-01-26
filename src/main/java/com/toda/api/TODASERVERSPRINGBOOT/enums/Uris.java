package com.toda.api.TODASERVERSPRINGBOOT.enums;

public enum Uris {
    /** 1. 자체 로그인 API */ POST_LOGIN,
    /** 1-1. 자체 로그인 API Ver2 */ POST_LOGIN_VER2,
    /** 1-2. 이메일 중복 확인 API */ POST_EMAIL_VALID,
    /** 1-3. 토큰 데이터 추출 API */ GET_TOKEN,
    /** 1-4. 토큰 데이터 추출 API Ver2 */ GET_TOKEN_VER2,
    /** 1-5. 토큰 암호 유효성 검사 API */ POST_TOKEN,
    /** 1-6. 알림 토큰 저장 API */ POST_NOTIFICATION,
    /** 1-7. 알림 토큰 저장 API Ver2 */ POST_NOTIFICATION_VER2,
    /** 1-8. 강제 업데이트 API */ GET_UPDATE,
    /** 1-9. 강제 업데이트 API Ver2 */ GET_UPDATE_VER2,
    /** 1-10. 유효성 확인 API */ GET_VALIDATION,
    /** 1-11. 자신의 이메일인지 확인 API */ POST_EMAIL_CHECK,
    /** 1-12. 약관 조회 API */ GET_TERMS,
    /** 2. 자체 회원가입 API */ POST_USER,
    /** 2-1. 자체 회원가입 API Ver2 */ POST_USER_VER2,
    /** 3. 회원탈퇴 API */ DELETE_USER,
    /** 4. 닉네임 변경 API */ PATCH_NAME,
    /** 5. 비밀번호 변경 API */ PATCH_PASSWORD,
    /** 5-1. 비밀번호 변경 API Ver2 */ PATCH_PASSWORD_VER2,
    /** 6. 유저 정보 변경 API */ PATCH_USER,
    /** 6-0. 프로필 사진 삭제 API */ DELETE_SELFIE,
    /** 7. 회원정보조회 API */ GET_USER,
    /** 7-0. 유저코드를 통한 회원정보 조회 API */ GET_USERCODE_UCVALUE_USER,
    /** 7-1. 유저코드를 통한 회원정보 조회 API Ver2 */ GET_USERCODE_UCVALUE_USER_VER2,
    /** 7-2. 유저 보유 스티커 조회 API */ GET_USER_STICKERS,
    /** 7-3. 임시 비밀번호 발급 API */ POST_USER_SEARCHPW,
    /** 7-5. 알림 허용 여부 확인 API(3개) */ GET_ALARM_VER2,
    /** 7-6. 알림 허용 여부 변경 API(3개) */ PATCH_ALARM_VER2,
    /** 7-7. 알림 시간 조회 API */ GET_ALARM_TIME,
    /** 7-8. 알림 시간 변경 API */ PATCH_ALARM_TIME,
    /** 8. 앱 비밀번호 설정 API */ POST_LOCK,
    /** 8-1. 앱 비밀번호 설정 API Ver2 */ POST_LOCK_VER2,
    /** 9. 앱 비밀번호 해제 API */ DELETE_LOCK,
    /** 9-1. 앱 비밀번호 해제 API Ver2 */ DELETE_LOCK_VER2,
    /** 10. 알림 조회 API */ GET_LOG,
    /** 11. 다이어리 추가 API */ POST_DIARY,
    /** 12. 다이어리 유저 추가 API */ POST_DIARIES_NUMBER_USER,
    /** 12-1. 유저에게 온 다이어리 초대 요청 조회 API */ GET_LOG_NUMBER,
    /** 13. 다이어리 퇴장 및 초대 거절 API */ DELETE_DIARY_NUMBER,
    /** 14. 다이어리 수정 API */ PATCH_DIARY,
    /** 15. 다이어리 조회 API */ GET_DIARIES,
    /** 15-0. 다이어리 멤버 조회 API */ GET_DIARIES_NUMBER_USERS,
    /** 15-1. 다이어리 공지 등록 API */ POST_NOTICE,
    /** 15-2. 다이어리 공지 삭제 API */ DELETE_NOTICE_NUMBER,
    /** 15-3. 다이어리 공지 수정 API */ PATCH_NOTICE,
    /** 15-4. 다이어리 공지 조회 API */ GET_NOTICE_NUMBER,
    /** 16-2. 게시물 작성 API(날짜 폰트 추가) */ POST_POST_VER3,
    /** 16-3. 게시물 작성 API(게시글 ID 리턴) */ POST_POST_VER5,
    /** 17. 게시물 삭제 API */ DELETE_POST_NUMBER,
    /** 18-2. 게시물 수정 API */ PATCH_POST_VER3,
    /** 19. 게시물 리스트 조회 API */ GET_DIARIES_NUMBER_POSTS,
    /** 20-1. 게시물 상세 조회 API(날짜 및 폰트 추가 버전) */ GET_POSTS_NUMBER_VER2,
    /** 28. 좋아요 API */ POST_POSTS_NUMBER_LIKE,
    /** 22. 스티커 사용 API */ POST_POSTS_NUMBER_STICKERS,
    /** 22-1. 스티커 사용 API Ver2 */ POST_STICKER_VER2,
    /** 23. 스티커 수정 API */ PATCH_POSTS_NUMBER_STICKERS,
    /** 24. 스티커 상세 조회 API */ GET_STICKERS_NUMBER,
    /** 25. 사용자가 사용한 스티커 리스트 조회 API */ GET_POSTS_NUMBER_STICKERS,
    /** 30. 댓글 작성 API */ POST_COMMENT,
    /** 30-1. 댓글 작성 API(댓글 ID 리턴) */ POST_COMMENT_VER3,
    /** 31. 댓글 삭제 API */ DELETE_COMMENT_NUMBER,
    /** 32. 댓글 수정 API */ PATCH_COMMENT,
    /** 33. 댓글 리스트 조회 API */ GET_POSTS_NUMBER_COMMENTS,
    /** 38. 공지사항 리스트 조회 API */ GET_ANNOUNCEMENT,
    /** 39. 공지사항 상세 조회 API */ GET_ANNOUNCEMENT_NUMBER,
    /** 40. 공지사항 읽었는지 안읽었는지 확인 API */ GET_ANNOUNCEMENT_CHECK,


    /** 다이어리 초대 알림 FCM 데이터 조회 */ GET_FCM_INVITE,
    /** 다이어리 승낙 알림 FCM 데이터 조회 */ GET_FCM_ACCEPT,
    /** 게시글 작성 알림 FCM 데이터 조회 */ GET_FCM_POST,
    /** 좋아요 알림 FCM 데이터 조회 */ GET_FCM_LIKE,
    /** 댓글 알림 FCM 데이터 조회 */ GET_FCM_COMMENT,
    /** 대댓글 알림 FCM 데이터 조회 */ GET_FCM_REPLY;






    // 관리자 API 만들기
    // 1. 환불 API
    // 2. 삭제 일기 복구 API
    // 3. 삭제 일기 조회 API
    // 4. Redis 관련 API


//        $r->addRoute('GET', '/user/stickers', ['StickerController', 'getUserStickers']);                                        //7-1. 유저 보유 스티커 조회 API(스티커 Controller에 존재)
//
//        $r->addRoute('POST', '/notification', ['NotificationController', 'checkNotification']);                                 //1-5. 알림 토큰 저장 API
//        $r->addRoute('GET', '/alarm', ['NotificationController', 'checkAlarm']);                                                //1-7. 알림 허용 여부 확인 API
//        $r->addRoute('PATCH', '/alarm', ['NotificationController', 'updateAlarm']);                                             //1-8. 알림 허용 여부 변경 API
//        $r->addRoute('GET', '/alarm/ver2', ['NotificationController', 'checkAlarmVer2']);                                       //7-5. 알림 허용 여부 확인 API(3개)
//        $r->addRoute('PATCH', '/alarm/ver2', ['NotificationController', 'updateAlarmVer2']);                                    //7-6. 알림 허용 여부 변경 API(3개)
//        $r->addRoute('GET', '/alarm/time', ['NotificationController', 'getAlarmTime']);                                         //7-7. 알림 시간 조회 API
//        $r->addRoute('PATCH', '/alarm/time', ['NotificationController', 'updateAlarmTime']);                                    //7-8. 알림 시간 변경 API
//
//        $r->addRoute('POST', '/diary', ['DiaryController', 'addDiary']);                                                        //11. 다이어리 추가 API
//        $r->addRoute('POST', '/diaries/{diaryID:\d+}/user', ['DiaryController', 'addDiaryFriend']);                             //12. 다이어리 유저 추가 API
//        $r->addRoute('GET', '/log/{diaryID:\d+}', ['DiaryController', 'getRequestByUserCode']);                                 //12-1. 유저코드 유저 조회 API
//        $r->addRoute('DELETE', '/diary/{diaryID:\d+}', ['DiaryController', 'deleteDiary']);
//        $r->addRoute('PATCH', '/diary', ['DiaryController', 'updateDiary']);
//        $r->addRoute('GET', '/diaries', ['DiaryController', 'getDiaries']);                                                     //15. 다이어리 조회 API
//        $r->addRoute('GET', '/diaries/{diaryID:\d+}/users', ['DiaryController', 'getDiariesMember']);                           //15-0. 다이어리 멤버 조회 API
//
//        $r->addRoute('PATCH', '/notice', ['NoticeController', 'updateNotice']);                                                 //15-3. 다이어리 공지 수정 API
//        $r->addRoute('GET', '/notice/{diaryID:\d+}', ['NoticeController', 'getNotice']);                                        //15-4. 다이어리 공지 조회 API
//
//        // $r->addRoute('POST', '/post', ['PostController', 'addPostOld']);                                                        //16. 게시물 작성 API Deprecated
//        // $r->addRoute('POST', '/post/ver2', ['PostController', 'addPost']);                                                      //16-1. 게시물 작성 API(이미지 추가) Deprecated
//        $r->addRoute('POST', '/post/ver3', ['PostController', 'addPostVer3']);                                                  //16-2. 게시물 작성 API(날짜 폰트 추가)
//        $r->addRoute('DELETE', '/post/{postID:\d+}', ['PostController', 'deletePost']);
//        $r->addRoute('PATCH', '/post/ver3', ['PostController', 'updatePostVer3']);
//        // $r->addRoute('PATCH', '/post', ['PostController', 'updatePostOld']);                                                    //18. 게시물 수정 API Deprecated
//        $r->addRoute('PATCH', '/post/ver2', ['PostController', 'updatePost']);                                                  //18-1. 게시물 수정 API(이미지 추가)
//
//        $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts', ['PostController', 'getPostList']);                                 //19. 게시물 리스트 조회 API
//        $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/ver2', ['PostController', 'getPostListNew']);                         //19-0. 게시물 리스트 조회 API(날짜)
//        $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/countbydate', ['PostController', 'getPostNumByDate']);                //19-1. 게시물 날짜별 개수 조회 API
//        // $r->addRoute('GET', '/posts/{postID:\d+}', ['PostController', 'getPostDetail']);                                        //20. 게시물 상세 조회 API Deprecated
//        $r->addRoute('GET', '/posts/{postID:\d+}/ver2', ['PostController', 'getPostDetailVer2']);                               //20-1. 게시물 상세 조회 API(날짜 및 폰트 추가 버전)
//
//        $r->addRoute('POST', '/posts/{postID:\d+}/stickers', ['StickerController', 'addSticker']);                              //22. 스티커 사용 API
//        $r->addRoute('PATCH', '/posts/{postID:\d+}/stickers', ['StickerController', 'updateSticker']);                          //23. 스티커 수정 API
//        $r->addRoute('GET', '/stickers/{stickerPackID:\d+}', ['StickerController', 'getStickerDetail']);                        //24-1. 스티커 상세 조회 API
//        $r->addRoute('GET', '/store', ['StickerController', 'getStore']);                                                       //24-2. 스티커 상점 조회 API
//        $r->addRoute('GET', '/posts/{postID:\d+}/stickers', ['StickerController', 'getStickerView']);                           //25. 사용자가 사용한 스티커 리스트 조회 API
//
//        $r->addRoute('GET', '/points', ['PointController', 'getPoint']);                                                        //27. 포인트 조회 API
//
//        $r->addRoute('POST', '/posts/{postID:\d+}/like', ['LikeCommentController', 'postLike']);                                //28. 좋아요 API
//        $r->addRoute('POST', '/comment', ['LikeCommentController', 'postComment']);                                             //30. 댓글 작성 API
//        $r->addRoute('DELETE', '/comment/{commentID:\d+}', ['LikeCommentController', 'deleteComment']);
//        $r->addRoute('PATCH', '/comment', ['LikeCommentController', 'updateComment']);
//        $r->addRoute('GET', '/posts/{postID:\d+}/comments', ['LikeCommentController', 'getComment']);                           //33. 댓글 리스트 조회 API
//
//
//
//        $r->addRoute('GET', '/diaries/{diaryID:\d+}/schedule', ['CalenderController', 'getSchedule']);                          //37. 일정 조회 API
//        $r->addRoute('GET', '/popup/{version}', ['LoginController', 'getPopupRead']);                                           //1-9. 업데이트 공지 읽었는지 확인 API
//        $r->addRoute('PATCH', '/popup/{version}', ['LoginController', 'updatePopupRead']);                                      //1-10. 업데이트 공지 읽기 API
//        $r->addRoute('POST', '/user/{code:\d+}', ['LoginController', 'postUserInfo']);                                          //2-1. 간편 회원가입 API
//        $r->addRoute('PATCH', '/birth', ['UserController', 'updateBirth']);                                                     //6-1. 생년월일 변경 API
}
