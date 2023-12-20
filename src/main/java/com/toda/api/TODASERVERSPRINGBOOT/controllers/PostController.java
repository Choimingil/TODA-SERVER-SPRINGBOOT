package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateDiary;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreatePost;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController extends AbstractController implements BaseController {
    private final PostService postService;

    //16-2. 게시물 작성 API(날짜 폰트 추가)
    @PostMapping("/post/ver3")
    public Map<String, ?> createPost(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid CreatePost createPost,
            BindingResult bindingResult
    ){
        long userID = postService.getUserID(token);
        int userDiaryStatus = postService.getUserDiaryStatus(userID,createPost.getDiary());

        // 현재 다이어리에 속해 있는 게시글 추가 작업 진행
        if(userDiaryStatus == 100){
            Post newPost = postService.addPost(userID, createPost);
            if(!createPost.getImageList().isEmpty())
                postService.addPostImage(newPost.getPostID(),createPost.getImageList());

            // 알림 발송
            UserData sendUserData = postService.getSendUserData(token);
            postService.setFcmAndLog(sendUserData,newPost,3);
            return new SuccessResponse.Builder(SuccessResponse.of.CREATE_POST_SUCCESS).build().getResponse();
        }

        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    // $r->addRoute('DELETE', '/post/{postID:\d+}', ['PostController', 'deletePost']);
    // $r->addRoute('PATCH', '/post/ver3', ['PostController', 'updatePostVer3']);                                               //18-2. 게시물 수정 API
    // $r->addRoute('POST', '/posts/{postID:\d+}/like', ['LikeCommentController', 'postLike']);                                //28. 좋아요 API

    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts', ['PostController', 'getPostList']);                                 //19. 게시물 리스트 조회 API
    // $r->addRoute('GET', '/posts/{postID:\d+}/ver2', ['PostController', 'getPostDetailVer2']);                               //20-1. 게시물 상세 조회 API(날짜 및 폰트 추가 버전)

    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/ver2', ['PostController', 'getPostListNew']);                         //19-0. 게시물 리스트 조회 API(날짜)
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/countbydate', ['PostController', 'getPostNumByDate']);                //19-1. 게시물 날짜별 개수 조회 API
}
