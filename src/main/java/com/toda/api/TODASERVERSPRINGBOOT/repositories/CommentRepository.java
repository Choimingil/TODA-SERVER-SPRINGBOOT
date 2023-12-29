package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    boolean existsByCommentIDAndPostID(long commentID, long postID);
    List<Comment> findByParentIDAndStatusNot(long parentID, int status);
    Comment findByCommentID(long commentID);
}
