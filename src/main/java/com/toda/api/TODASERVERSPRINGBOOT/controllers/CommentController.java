package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class CommentController extends AbstractController implements BaseController {
    // $r->addRoute('POST', '/posts/{postID:\d+}/like', ['LikeCommentController', 'postLike']);                                //28. 좋아요 API
    // $r->addRoute('POST', '/comment', ['LikeCommentController', 'postComment']);                                             //30. 댓글 작성 API
    // $r->addRoute('DELETE', '/comment/{commentID:\d+}', ['LikeCommentController', 'deleteComment']);
    // $r->addRoute('PATCH', '/comment', ['LikeCommentController', 'updateComment']);
    // $r->addRoute('GET', '/posts/{postID:\d+}/comments', ['LikeCommentController', 'getComment']);                           //33. 댓글 리스트 조회 API
}
