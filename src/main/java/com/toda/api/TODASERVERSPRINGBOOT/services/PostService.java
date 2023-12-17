package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostImage;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostText;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreatePost;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.PostImageRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.PostRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.PostTextRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserDiaryRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractFcmService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component("postService")
@RequiredArgsConstructor
public class PostService extends AbstractFcmService implements BaseService {
    private final UserDiaryRepository userDiaryRepository;
    private final PostRepository postRepository;
    private final PostTextRepository postTextRepository;
    private final PostImageRepository postImageRepository;

    private final TokenProvider tokenProvider;

    @Transactional
    public long addPost(long userID, CreatePost createPost){
        int status = getStatus(createPost.getBackground(), createPost.getMood(), () -> {});

        Post post = new Post();
        post.setUserID(userID);
        post.setDiaryID(createPost.getDiary());
        post.setTitle(createPost.getTitle());
        post.setStatus(status);
        post.setCreateAt(toLocalDateTime(createPost.getDate()));
        Post newPost = postRepository.save(post);

        long postID = newPost.getPostID();
        addPostText(postID,createPost);
        return postID;
    }

    @Transactional
    private void addPostText(long postID, CreatePost createPost){
        int status = getStatus(createPost.getAligned(), createPost.getFont(), () -> {});

        PostText postText = new PostText();
        postText.setPostID(postID);
        postText.setText(createPost.getText());
        postText.setStatus(status);
        postTextRepository.save(postText);
    }

    @Transactional
    public void addPostImage(long postID, List<String> imageList){
        List<PostImage> postImageList = new ArrayList<>();
        for(String image : imageList){
            PostImage postImage = new PostImage();
            postImage.setPostID(postID);
            postImage.setUrl(image);
            postImageList.add(postImage);
        }
        postImageRepository.saveAll(postImageList);
    }






    public long getUserID(String token){return getUserID(token, tokenProvider);}
    public int getUserDiaryStatus(long userID, long diaryID){return getUserDiaryStatus(userID, diaryID, userDiaryRepository);}
}
