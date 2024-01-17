package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.PostDetail;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.PostList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Post findByPostID(long postID);

    @Query("SELECT p as post, " +
            "IFNULL(h.isMyLike, 0) AS isMyLike, " +
            "IFNULL(h.likeNum, 0) AS likeNum, " +
            "IFNULL(c.commentNum, 0) AS commentNum " +
            "FROM Post p " +
            "LEFT JOIN (SELECT h.postID as postID, IF(h.userID = :userID, 1, 0) AS isMyLike, COUNT(h) AS likeNum FROM Heart h WHERE h.status NOT LIKE 0 GROUP BY h.postID) " +
            "AS h ON p.postID = h.postID " +
            "LEFT JOIN (SELECT c.postID as postID, COUNT(c) AS commentNum FROM Comment c WHERE c.status NOT LIKE 0 GROUP BY c.postID) " +
            "AS c ON p.postID = c.postID " +
            "WHERE p.diaryID = :diaryID AND p.status NOT LIKE 0 ORDER BY p.createAt DESC")
    List<PostList> getPostList(long userID, long diaryID, Pageable pageable);

    @Query("SELECT p as post, pt as postText, " +
            "IFNULL(h.isMyLike, 0) AS isMyLike, " +
            "IFNULL(h.likeNum, 0) AS likeNum, " +
            "IFNULL(c.commentNum, 0) AS commentNum " +
            "FROM Post p " +
            "INNER JOIN PostText pt ON pt.postID = p.postID " +
            "LEFT JOIN (SELECT h.postID as postID, IF(h.userID = :userID, 1, 0) AS isMyLike, COUNT(h) AS likeNum FROM Heart h WHERE h.status NOT LIKE 0 GROUP BY h.postID) " +
            "AS h ON p.postID = h.postID " +
            "LEFT JOIN (SELECT c.postID as postID, COUNT(c) AS commentNum FROM Comment c WHERE c.status NOT LIKE 0 GROUP BY c.postID) " +
            "AS c ON p.postID = c.postID " +
            "WHERE p.postID = :postID")
    PostDetail getPostDetail(long userID, long postID);
}
