package com.fineapple.toda.api.entities.mappings;

import com.fineapple.toda.api.entities.Post;

public interface PostList {
    Post getPost();
    int getIsMyLike();
    int getLikeNum();
    int getCommentNum();
}
