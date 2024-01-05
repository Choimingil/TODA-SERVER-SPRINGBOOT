package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.repositories.CommentRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.PostRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserDiaryRepository;

public interface BaseStatus {
    /**
     * 유저가 다이어리에 어떤 상태로 존재하는지 확인
     * @param userID
     * @param diaryID
     * @return 404,100,200
     * 404 : 유저가 다이어리에 속하지 않을 경우
     * 100 : 유저가 다이어리에 속할 경우
     * 200 : 유저가 다이어리에 속하지 않고 초대 요청이 온 경우
     */
    int getUserDiaryStatus(long userID, long diaryID, UserDiaryRepository userDiaryRepository);

    /**
     * 유저의 게시글 접근 권한 확인
     * @param userID
     * @param postID
     * @param userDiaryRepository
     * @param postRepository
     * @return 404,100,200
     * 404 : 유저가 다이어리에 속하지 않을 경우
     * 100 : 유저가 작성한 게시글일 경우
     * 200 : 유저가 게시글이 존재하는 다이어리에 속하지만 유저가 작성한 게시글이 아닌 경우
     */
    int getUserPostStatus(long userID, long postID, UserDiaryRepository userDiaryRepository, PostRepository postRepository);

    /**
     * 유저의 댓글 접근 권한 확인
     * @param userID
     * @param commentID
     * @param commentRepository
     * @return
     * 404 : 유저가 작성하지 않은 댓글일 경우
     * 100 : 유저가 작성한 댓글일 경우
     * 200 : 유저가 작성한 대댓글일 경우
     */
    int getUserCommentStatus(long userID, long commentID, CommentRepository commentRepository);

    /**
     * 데이터의 상태값을 생성
     * @param firstValue
     * @param secondValue
     * @param secondValue
     * @param digit
     * @return firstValue*100 + secondValue 형식
     */
    int getStatus(int firstValue, int secondValue, int digit, MethodParamsInterface.MethodNoParams params);
}
