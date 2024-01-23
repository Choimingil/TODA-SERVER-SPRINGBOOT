package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Comment;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.CommentDetail;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.PostDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    boolean existsByCommentIDAndPostID(long commentID, long postID);
    List<Comment> findByParentIDAndStatusNot(long parentID, int status);
    Comment findByCommentID(long commentID);
    int countByPostIDAndStatusNot(long postID, int status);




    @Query("SELECT c as comment, ui.url as selfie FROM Comment c " +
            "INNER JOIN UserImage ui on c.userID = ui.userID and ui.status not like 0 " +
            "where c.postID = :postID and c.status not like 0")
    List<CommentDetail> getCommentDetail(long postID, Pageable pageable);

    @Query("SELECT c as comment, ui.url as selfie FROM Comment c " +
            "INNER JOIN UserImage ui on c.userID = ui.userID and ui.status not like 0 " +
            "where c.parentID in :parentIDList and c.status not like 0")
    List<CommentDetail> getReCommentDetail(List<Long> parentIDList);
}
