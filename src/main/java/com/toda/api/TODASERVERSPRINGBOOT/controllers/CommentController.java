package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.SetMdcBody;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Comment;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateComment;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdateComment;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.CommentListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CommentController extends AbstractController implements BaseController {
    private final CommentService commentService;

    public CommentController(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus, DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth, CommentService commentService) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateUserAuth);
        this.commentService = commentService;
    }

    //30. 댓글 작성 API
    @PostMapping("/comment")
    @SetMdcBody
    public Map<String, ?> createComment(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid CreateComment createComment,
            @RequestParam(name="comment", required = false) Long comment,
            @RequestParam(name="type", required = false) String type,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,createComment.getPost());

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            UserDetail sendUser = getUserInfo(token);

            // 부모 댓글 아이디가 존재하지 않으면 댓글 작성 진행
            if(comment == null){
                Comment target = commentService.addComment(userID, createComment.getPost(), createComment.getReply());
                commentService.setFcmAndLog(commentService.getFcmAddCommentUserMap(userID, target),sendUser,target,5);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_COMMENT_SUCCESS).build().getResponse();
            }
            // 부모 댓글 아이디가 존재한다면 대댓글 작성 진행
            else{
                // 부모 댓글 아이디가 해당 게시글의 댓글이 아닐 경우 예외 리턴
                if(!commentService.isValidCommentPostDiary(comment,createComment.getPost()))
                    throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_COMMENT_EXCEPTION);

                Comment target = commentService.addReComment(userID, createComment.getPost(), createComment.getReply(), comment);
                commentService.setFcmAndLog(commentService.getFcmAddReCommentUserMap(userID, comment),sendUser,target,6);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_RE_COMMENT_SUCCESS).build().getResponse();
            }
        }
    }

    //30-1. 댓글 작성 API(댓글 ID 리턴)
    @PostMapping("/comment/ver3")
    @SetMdcBody
    public Map<String, ?> createCommentVer2(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid CreateComment createComment,
            @RequestParam(name="comment", required = false) Long comment,
            @RequestParam(name="type", required = false) String type,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,createComment.getPost());

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            UserDetail sendUser = getUserInfo(token);

            // 부모 댓글 아이디가 존재하지 않으면 댓글 작성 진행
            if(comment == null){
                Comment target = commentService.addComment(userID, createComment.getPost(), createComment.getReply());
                commentService.setFcmAndLog(commentService.getFcmAddCommentUserMap(userID, target),sendUser,target,5);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_COMMENT_SUCCESS)
                        .add("commentID",target.getCommentID())
                        .build().getResponse();
            }
            // 부모 댓글 아이디가 존재한다면 대댓글 작성 진행
            else{
                // 부모 댓글 아이디가 해당 게시글의 댓글이 아닐 경우 예외 리턴
                if(!commentService.isValidCommentPostDiary(comment,createComment.getPost()))
                    throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_COMMENT_EXCEPTION);

                Comment target = commentService.addReComment(userID, createComment.getPost(), createComment.getReply(), comment);
                commentService.setFcmAndLog(commentService.getFcmAddReCommentUserMap(userID, comment),sendUser,target,6);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_RE_COMMENT_SUCCESS)
                        .add("commentID",target.getCommentID())
                        .build().getResponse();
            }
        }
    }

    //31. 댓글 삭제 API
    @DeleteMapping("/comment/{commentID}")
    public Map<String, ?> deleteComment(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("commentID") long commentID
    ){
        long userID = getUserID(token);
        int userCommentStatus = getUserCommentStatus(userID,commentID);

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
    @SetMdcBody
    public Map<String, ?> updateComment(
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @RequestBody @Valid UpdateComment updateComment,
            BindingResult bindingResult
    ){
        long userID = getUserID(token);
        int userCommentStatus = getUserCommentStatus(userID, updateComment.getComment());

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
            @RequestHeader(DelegateJwt.HEADER_NAME) String token,
            @PathVariable("postID") long postID,
            @RequestParam(name="page", required = true) int page
    ){
        long userID = getUserID(token);
        int userPostStatus = getUserPostStatus(userID,postID);

        // 현재 게시글에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            CommentListResponse res = commentService.getCommentList(userID, postID, page);

            // 댓글이 존재하지 않을 경우 메시지 출력
            if(res.getComment().isEmpty()){
                return new SuccessResponse.Builder(
                        SuccessResponse.of.GET_SUCCESS.getCode(),
                        "등록된 댓글이 없습니다."
                )
                        .add("result",res)
                        .build().getResponse();
            }
            // 댓글이 존재할 경우 댓글 리스트 출력
            else return new SuccessResponse.Builder(SuccessResponse.of.GET_SUCCESS)
                    .add("result",res)
                    .build().getResponse();
        }
    }
}
