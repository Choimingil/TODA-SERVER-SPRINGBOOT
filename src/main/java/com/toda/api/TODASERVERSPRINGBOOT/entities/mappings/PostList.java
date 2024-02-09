package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;

public interface PostList {
    Post getPost();
    int getIsMyLike();
    int getLikeNum();
    int getCommentNum();
}
