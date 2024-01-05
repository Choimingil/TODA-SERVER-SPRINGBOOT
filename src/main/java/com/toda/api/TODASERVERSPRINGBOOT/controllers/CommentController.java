package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateComment;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateComment;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.CommentListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CommentController extends AbstractController implements BaseController {
    private final CommentService commentService;

    public CommentController(
            DelegateDateTime delegateDateTime,
            DelegateJwt delegateJwt,
            CommentService commentService
    ) {
        super(delegateDateTime, delegateJwt);
        this.commentService = commentService;
    }

    //30. 댓글 작성 API
    @PostMapping("/comment")
    public Map<String, ?> createComment(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid CreateComment createComment,
            @RequestParam(name="comment", required = false) Long comment,
            @RequestParam(name="type", required = false) String type,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userPostStatus = commentService.getUserPostStatus(userID,createComment.getPost());

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            UserData sendUserData = commentService.getSendUserData(token);

            // 부모 댓글 아이디가 존재하지 않으면 댓글 작성 진행
            if(comment == null){
                com.toda.api.TODASERVERSPRINGBOOT.entities.Comment target = commentService.addComment(userID, createComment.getPost(), createComment.getReply());
                commentService.setFcmAndLog(commentService.getFcmAddCommentUserMap(userID, target),sendUserData,target,5);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_COMMENT_SUCCESS).build().getResponse();
            }
            // 부모 댓글 아이디가 존재한다면 대댓글 작성 진행
            else{
                // 부모 댓글 아이디가 해당 게시글의 댓글이 아닐 경우 예외 리턴
                if(!commentService.isValidCommentPostDiary(comment,createComment.getPost()))
                    throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_COMMENT_EXCEPTION);

                com.toda.api.TODASERVERSPRINGBOOT.entities.Comment target = commentService.addReComment(userID, createComment.getPost(), createComment.getReply(), comment);
                commentService.setFcmAndLog(commentService.getFcmAddReCommentUserMap(userID, comment),sendUserData,target,6);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_RE_COMMENT_SUCCESS).build().getResponse();
            }
        }
    }

    //31. 댓글 삭제 API
    @DeleteMapping("/comment/{commentID}")
    public Map<String, ?> deleteComment(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("commentID") long commentID
    ){
        long userID = getUserID(token);
        int userCommentStatus = commentService.getUserCommentStatus(userID,commentID);

        // 자신이 작성한 댓글인지 확인
        if(userCommentStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_USER_COMMENT_EXCEPTION);
        else{
            // 자신이 작성한 댓글인 경우 삭제 진행
            commentService.deleteComment(commentID);
            return new SuccessResponse.Builder(SuccessResponse.of.DELETE_COMMENT_SUCCESS).build().getResponse();
        }
    }
    
    //32. 댓글 수정 API
    @PatchMapping("/comment")
    public Map<String, ?> updateComment(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid UpdateComment updateComment,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userCommentStatus = commentService.getUserCommentStatus(userID, updateComment.getComment());

        // 자신이 작성한 댓글인지 확인
        if(userCommentStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_USER_COMMENT_EXCEPTION);
        else{
            // 자신이 작성한 댓글인 경우 수정 진행
            commentService.updateComment(updateComment.getComment(), updateComment.getReply());
            return new SuccessResponse.Builder(SuccessResponse.of.UPDATE_COMMENT_SUCCESS).build().getResponse();
        }
    }

    //33. 댓글 리스트 조회 API
    @GetMapping("/posts/{postID}/comments")
    public Map<String, ?> getPostList(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @PathVariable("postID") long postID,
            @RequestParam(name="page", required = true) int page
    ){
        long userID = getUserID(token);
        int userPostStatus = commentService.getUserPostStatus(userID,postID);

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            CommentListResponse res = commentService.getCommentList(userID, postID, page);

            // 댓글이 존재하지 않을 경우 메시지 출력
            if(res.getComment().isEmpty()){
                Map<String,String> emptyRes = new HashMap<>();
                emptyRes.put("message","등록된 댓글이 없습니다.");
                return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                        .add("result",emptyRes)
                        .build().getResponse();
            }
            // 댓글이 존재할 경우 댓글 리스트 출력
            else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",res)
                    .build().getResponse();
        }
    }
}
