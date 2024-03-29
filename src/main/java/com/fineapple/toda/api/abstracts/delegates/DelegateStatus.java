package com.fineapple.toda.api.abstracts.delegates;

import com.fineapple.toda.api.abstracts.interfaces.BaseStatus;
import com.fineapple.toda.api.entities.Post;
import com.fineapple.toda.api.repositories.CommentRepository;
import com.fineapple.toda.api.entities.Comment;
import com.fineapple.toda.api.entities.UserDiary;
import com.fineapple.toda.api.repositories.PostRepository;
import com.fineapple.toda.api.repositories.UserDiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class DelegateStatus implements BaseStatus {
    private final UserDiaryRepository userDiaryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    @Override
    public int getUserDiaryStatus(long userID, long diaryID) {
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        if(userDiaryList.isEmpty()) return 404;
        for(UserDiary userDiary : userDiaryList) if(userDiary.getStatus()%10 != 0) return 100;
        return 200;
    }

    @Override
    public int getUserPostStatus(long userID, long postID) {
        Post post = postRepository.findByPostID(postID);
        if(post == null) return 404;

        if(post.getUserID() == userID) return 100;
        else{
            List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,post.getDiaryID(),999);
            if(userDiaryList.isEmpty()) return 404;
            else return 200;
        }
    }

    @Override
    public int getUserCommentStatus(long userID, long commentID) {
        Comment comment = commentRepository.findByCommentID(commentID);
        if(comment == null || comment.getUserID() != userID) return 404;
        return comment.getParentID() == 0 ? 100 : 200;
    }

    @Override
    public int getStatus(int firstValue, int secondValue, int digit, Runnable runnable) {
        runnable.run();
        return firstValue*digit + secondValue;
    }
}
