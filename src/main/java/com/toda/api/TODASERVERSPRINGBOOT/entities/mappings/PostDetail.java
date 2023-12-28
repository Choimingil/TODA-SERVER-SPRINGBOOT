package com.toda.api.TODASERVERSPRINGBOOT.entities.mappings;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostText;

public interface PostDetail {
    Post getPost();
    PostText getPostText();
    int getIsMyLike();
    int getLikeNum();
    int getCommentNum();
}
