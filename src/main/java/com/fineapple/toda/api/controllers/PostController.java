package com.fineapple.toda.api.controllers;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.entities.Post;
import com.fineapple.toda.api.entities.Heart;
import com.fineapple.toda.api.models.bodies.CreatePost;
import com.fineapple.toda.api.models.bodies.SetHeart;
import com.fineapple.toda.api.models.bodies.UpdatePost;
import com.fineapple.toda.api.models.responses.get.PostListResponse;
import com.fineapple.toda.api.services.PostService;
import com.fineapple.toda.api.abstracts.AbstractController;
import com.fineapple.toda.api.abstracts.interfaces.BaseController;
import com.fineapple.toda.api.annotations.SetMdcBody;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import com.fineapple.toda.api.exceptions.BusinessLogicException;
import com.fineapple.toda.api.models.responses.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PostController extends AbstractController implements BaseController {
    private final PostService postService;

    public PostController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, PostService postService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.postService = postService;
    }

    //16-2. 게시물 작성 API(날짜 폰트 추가)
    @PostMapping("/post/ver3")
    @SetMdcBody
    public Map<String, ?> createPost(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid CreatePost createPost,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,createPost.getDiary());

        // 현재 다이어리에 속해 있는 경우 게시글 추가 작업 진행
        if(userDiaryStatus == 100){
            Post target = postService.addPost(userID, createPost);

            if(!createPost.getImageList().isEmpty())
                postService.addPostImage(target.getPostID(),createPost.getImageList());

            // 알림 발송
            UserDetail sendUser = getUserInfo(token);
            postService.setFcmAndLog(postService.getFcmAddPostUserMap(userID, createPost.getDiary()),sendUser,target,3);
            return new SuccessResponse.Builder(SuccessResponse.of.CREATE_POST_SUCCESS).build().getResponse();
        }

        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    //16-3. 게시물 작성 API(게시글 ID 리턴)
    @PostMapping("/post/ver5")
    @SetMdcBody
    public Map<String, ?> createPostVer4(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid CreatePost createPost,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,createPost.getDiary());

        // 현재 다이어리에 속해 있는 경우 게시글 추가 작업 진행
        if(userDiaryStatus == 100){
            Post target = postService.addPost(userID, createPost);

            if(!createPost.getImageList().isEmpty())
                postService.addPostImage(target.getPostID(),createPost.getImageList());

            return new SuccessResponse.Builder(SuccessResponse.of.CREATE_POST_SUCCESS)
                    .add("postID",target.getPostID())
                    .build().getResponse();
        }

        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    //17. 게시물 삭제 API
    @DeleteMapping("/post/{postID}")
    public Map<String, ?> deletePost(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("postID") long postID
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,postID);

        // 자신이 작성한 게시글인지 확인
        if(userPostStatus == 100){
            // 자신이 작성한 게시글인 경우 삭제 진행
            postService.deletePost(postID);
            return new SuccessResponse.Builder(SuccessResponse.of.DELETE_POST_SUCCESS).build().getResponse();
        }
        else throw new BusinessLogicException(BusinessLogicException.of.NO_USER_POST_EXCEPTION);
    }

    //18-2. 게시물 수정 API
    @PatchMapping("/post/ver3")
    @SetMdcBody
    public Map<String, ?> updatePost(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdatePost updatePost,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,updatePost.getPost());

        // 자신이 작성한 게시글인지 확인
        if(userPostStatus == 100){
            // 자신이 작성한 게시글인 경우 수정 진행
            postService.updatePost(updatePost);
            postService.updatePostText(updatePost);
            postService.deletePostImage(updatePost.getPost());

            // 이미지가 존재한다면 추가 진행 (바로 전 단계에서 이미지 초기화 진행 완료)
            if(!updatePost.getImageList().isEmpty()) postService.addPostImage(updatePost.getPost(), updatePost.getImageList());
            return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_POST_SUCCESS).build().getResponse();
        }
        else throw new BusinessLogicException(BusinessLogicException.of.NO_USER_POST_EXCEPTION);
    }

    //28. 좋아요 API
    @PostMapping("/posts/{postID}/like")
    @SetMdcBody
    public Map<String, ?> setHeart(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid SetHeart setHeart,
            @PathVariable("postID") long postID,
            @RequestParam(name="type", required = false) String type,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,postID);

        // 현재 다이어리에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        // 그 외의 경우 좋아요 로직 수행
        else{
            // 현재 좋아요 리스트 가져오기
            List<Heart> heartList = postService.getHeartList(userID,postID);

            // 좋아요 리스트가 비어있을 경우 좋아요 최초 추가
            // 최초 1회 좋아요 시에만 알림 발송
            if(heartList.isEmpty()){
                postService.addHeart(userID,postID,setHeart.getMood());
                Post target = postService.getPostByID(postID);

                // 자신이 작성한 게시글이 아닐 경우 게시글 주인에게 알림 발송
                if(target.getUserID() != userID){
                    UserDetail sendUser = getUserInfo(token);
                    postService.setFcmAndLog(postService.getFcmAddHeartUserMap(userID, target.getUser()),sendUser,target,4);
                }

                return new SuccessResponse.Builder(SuccessResponse.of.DO_HEART_SUCCESS).build().getResponse();
            }
            else{
                // 하나의 좋아요 값만 가져오고 나머지 값은 제거
                Heart heart = postService.getValidHeart(heartList);
                if(heart == null) throw new BusinessLogicException(BusinessLogicException.of.WRONG_HEART_STATUS_EXCEPTION);

                // 좋아요 상태가 999일 경우 : 좋아요 취소 진행
                else if(heart.getStatus() == 999){
                    postService.updateHeart(heart,0);
                    return new SuccessResponse.Builder(SuccessResponse.of.UNDO_HEART_SUCCESS).build().getResponse();
                }
                // 좋아요 상태가 0일 경우 : 좋아요 다시 진행
                else if(heart.getStatus() == 0){
                    postService.updateHeart(heart, setHeart.getMood());
                    return new SuccessResponse.Builder(SuccessResponse.of.REDO_HEART_SUCCESS).build().getResponse();
                }
                // 그 외의 경우 잘못된 좋아요 리턴
                else throw new BusinessLogicException(BusinessLogicException.of.WRONG_HEART_STATUS_EXCEPTION);
            }
        }
    }

    //19. 게시물 리스트 조회 API
    @GetMapping("/diaries/{diaryID}/posts")
    public Map<String, ?> getPostList(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("diaryID") long diaryID,
            @RequestParam(name="page", required = true) int page
    ){
        long userID = getUserID(token);
        int userDiaryStatus = getUserDiaryStatus(userID,diaryID);

        // 현재 다이어리에 속해 있는 경우 게시글 조회 작업 진행
        if(userDiaryStatus == 100){
            List<PostListResponse> res = postService.getPostList(userID, diaryID, page);

            // 게시글이 존재하지 않을 경우 메시지 출력
            if(res.isEmpty()){
                return new SuccessResponse.Builder(
                        SuccessResponse.of.GET_SUCCESS.getCode(),
                        "등록된 게시글이 없습니다."
                )
                        .add("result",res)
                        .build().getResponse();
            }
            // 게시글이 존재할 경우 게시글 데이터 리턴
            else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",res)
                    .build().getResponse();
        }
        // 그 외의 경우 존재하지 않는 다이어리 리턴
        else throw new BusinessLogicException(BusinessLogicException.of.NO_DIARY_EXCEPTION);
    }

    //20-1. 게시물 상세 조회 API(날짜 및 폰트 추가 버전)
    @GetMapping("/posts/{postID}/ver2")
    public Map<String, ?> getPostDetail(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("postID") long postID
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,postID);

        // 현재 다이어리에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                .add("result",postService.getPostDetail(userID,postID))
                .build().getResponse();
    }






    // 이 부분은 일단 구현하지 않고 1. 안드로이드에서 사용하는지 2. 테스트 후 오류 발생하는지  이 부분 체크해서 구현
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/ver2', ['PostController', 'getPostListNew']);                         //19-0. 게시물 리스트 조회 API(날짜)
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/countbydate', ['PostController', 'getPostNumByDate']);                //19-1. 게시물 날짜별 개수 조회 API
}
