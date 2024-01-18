package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Comment;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.CommentDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.CommentDetailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.CommentListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.ReCommentDetailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("commentService")
public class CommentService extends AbstractService implements BaseService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateJms delegateJms,
            PostRepository postRepository,
            CommentRepository commentRepository,
            UserRepository userRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateJms);
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Comment addComment(long userID, long postID, String reply){
        Comment comment = new Comment();
        comment.setUserID(userID);
        comment.setPostID(postID);
        comment.setText(reply);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment addReComment(long userID, long postID, String reply, long parentID){
        Comment comment = new Comment();
        comment.setUserID(userID);
        comment.setPostID(postID);
        comment.setText(reply);
        comment.setParentID(parentID);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(long commentID){
        Comment comment = commentRepository.findByCommentID(commentID);
        comment.setStatus(0);
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(long commentID, String reply){
        Comment comment = commentRepository.findByCommentID(commentID);
        comment.setText(reply);
        commentRepository.save(comment);
    }

    @Transactional
    public void setFcmAndLog(Map<Long,String> map, UserData sendUserData, Comment comment, int type){
        Post post = comment.getPost() == null ? postRepository.findByPostID(comment.getPostID()) : comment.getPost();
        setJmsTopicFcm(
                sendUserData.getUserID(),
                (userID, userName) -> {
                    // 발송 조건 : 상대방 유저가 다이어리에 존재할 경우
                    return getUserDiaryStatus(userID,post.getDiaryID()) == 100;
                },
                // 조건 만족 시 FCM 발송
                (userID, userName) -> {
                    addUserLog(userID,sendUserData.getUserID(),post.getPostID(),type,100);
                    return getUserFcmTokenList(userID);
                },
                FcmDto.builder()
                        .title(getFcmTitle())
                        .body(getFcmBody(
                                sendUserData.getUserName(),
                                sendUserData.getUserCode(),
                                post.getUser() == null ? userRepository.findByUserID(post.getUserID()).getUserName() : post.getUser().getUserName(),
                                type))
                        .typeNum(type)
                        .dataID(post.getPostID())
                        .map(map)
                        .build()
        );
    }

    public CommentListResponse getCommentList(long userID, long postID, int page){
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
//        List<Comment> commentList = commentRepository.findByPostIDAndStatusNot(postID, 0, pageable);
        List<CommentDetail> commentList = commentRepository.getCommentDetail(postID,pageable);

        List<Long> commentIDList = commentList.stream().map(commentDetail -> {
            return commentDetail.getComment().getCommentID();
        }).toList();
//        List<Comment> reCommentList = commentRepository.findByParentIDInAndStatusNot(commentIDList,0);
        List<CommentDetail> reCommentList = commentRepository.getReCommentDetail(commentIDList);

        Map<Long, CommentDetailResponse> map = new HashMap<>();
        for(CommentDetail commentDetail : commentList){
            Comment comment = commentDetail.getComment();
            CommentDetailResponse response = CommentDetailResponse.builder()
                    .commentID(comment.getCommentID())
                    .postID(comment.getPostID())
                    .userID(comment.getUserID())
                    .userName(comment.getUser().getUserName())
                    .userSelfie(commentDetail.getSelfie())
                    .comment(comment.getText())
                    .time(getDateString(comment.getCreateAt()))
                    .isMyComment(comment.getUserID() == userID)
                    .build();
            map.put(comment.getCommentID(), response);
        }

        for(CommentDetail commentDetail : reCommentList){
            Comment reComment = commentDetail.getComment();
            ReCommentDetailResponse response = ReCommentDetailResponse.builder()
                    .commentID(reComment.getCommentID())
                    .comment(reComment.getText())
                    .userID(reComment.getUserID())
                    .userName(reComment.getUser().getUserName())
                    .userSelfie(commentDetail.getSelfie())
                    .parent(reComment.getParentID())
                    .time(getDateString(reComment.getCreateAt()))
                    .isMyComment(reComment.getUserID() == userID)
                    .build();
            map.get(reComment.getParentID()).getReComment().add(response);
        }

        List<CommentDetailResponse> commentDetailResponseList = new ArrayList<>();
        for (Map.Entry<Long, CommentDetailResponse> entry : map.entrySet()) {
            commentDetailResponseList.add(entry.getValue());
        }

        return CommentListResponse.builder()
                .totalCommentNum(commentRepository.countByPostIDAndStatusNot(postID,0))
                .comment(commentDetailResponseList)
                .build();
    }



    /**
     * 댓글 작성 시 FCM 발송받을 유저 데이터 getter
     * 발송 대상 : 게시글 주인
     * 이 때 자기 자신은 알림 대상에서 제외
     * @param comment
     * @return
     */
    public Map<Long,String> getFcmAddCommentUserMap(long userID, Comment comment){
        User user = comment.getUser() == null ? postRepository.findByPostID(comment.getPostID()).getUser() : comment.getUser();
        Map<Long,String> res = new HashMap<>();
        if(userID != user.getUserID()) res.put(user.getUserID(), user.getUserName());
        return res;
    }

    /**
     * 대댓글 작성 시 FCM 발송받을 유저 데이터 getter
     * 발송 대상 : 부모 댓글 주인 & 부모 댓글에 대댓글 남긴 모든 사람들
     * 이 때 자기 자신은 알림 대상에서 제외
     * @param parentID
     * @return
     */
    public Map<Long,String> getFcmAddReCommentUserMap(long userID, long parentID){
        Map<Long,String> res = getFcmReceiveUserMap(
                (comment,map)-> !map.containsKey(comment.getUserID()) && userID != comment.getUserID(),
                (comment,map)-> map.put(
                        comment.getUserID(),
                        comment.getUser().getUserName()
                ),
                commentRepository.findByParentIDAndStatusNot(parentID,0)
        );

        Comment parent = commentRepository.findByCommentID(parentID);
        res.put(parent.getUserID(), parent.getUser().getUserName());
        return res;
    }


    public boolean isValidCommentPostDiary(long commentID, long postID){return commentRepository.existsByCommentIDAndPostID(commentID,postID);}
}
