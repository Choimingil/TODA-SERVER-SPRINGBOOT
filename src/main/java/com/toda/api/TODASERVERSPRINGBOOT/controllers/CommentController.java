package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Comment;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateComment;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreatePost;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.SuccessResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController extends AbstractController implements BaseController {
    private final CommentService commentService;

    //30. 댓글 작성 API
    @PostMapping("/comment")
    public Map<String, ?> createComment(
            @RequestHeader(TokenProvider.HEADER_NAME) String token,
            @RequestBody @Valid CreateComment createComment,
            @RequestParam(name="comment", required = false) Long comment,
            @RequestParam(name="type", required = false) String type,
            BindingResult bindingResult
    ){
        long userID = commentService.getUserID(token);
        int userPostStatus = commentService.getUserPostStatus(userID,createComment.getPost());

        // 현재 다이어리에 속해 있지 않은 경우 게시물 볼 수 있는 권한 없음 리턴
        if(userPostStatus == 404) throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_POST_EXCEPTION);
        else{
            UserData sendUserData = commentService.getSendUserData(token);

            // 부모 댓글 아이디가 존재하지 않으면 댓글 작성 진행
            if(comment == null){
                Comment target = commentService.addComment(userID, createComment.getPost(), createComment.getReply());
                commentService.setFcmAndLog(commentService.getFcmAddCommentUserMap(userID, target),sendUserData,target,5);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_COMMENT_SUCCESS).build().getResponse();
            }
            // 부모 댓글 아이디가 존재한다면 대댓글 작성 진행
            else{
                // 부모 댓글 아이디가 해당 게시글의 댓글이 아닐 경우 예외 리턴
                if(!commentService.isValidCommentPostDiary(comment,createComment.getPost()))
                    throw new BusinessLogicException(BusinessLogicException.of.NO_AUTH_Comment_EXCEPTION);

                Comment target = commentService.addReComment(userID, createComment.getPost(), createComment.getReply(), comment);
                commentService.setFcmAndLog(commentService.getFcmAddReCommentUserMap(userID, comment),sendUserData,target,6);
                return new SuccessResponse.Builder(SuccessResponse.of.CREATE_RE_COMMENT_SUCCESS).build().getResponse();
            }
        }
    }

    // $r->addRoute('POST', '/comment', ['LikeCommentController', 'postComment']);                                             //30. 댓글 작성 API
    // $r->addRoute('DELETE', '/comment/{commentID:\d+}', ['LikeCommentController', 'deleteComment']);
    // $r->addRoute('PATCH', '/comment', ['LikeCommentController', 'updateComment']);
    // $r->addRoute('GET', '/posts/{postID:\d+}/comments', ['LikeCommentController', 'getComment']);                           //33. 댓글 리스트 조회 API
}
