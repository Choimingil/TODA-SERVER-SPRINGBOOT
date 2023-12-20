package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.Post;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostImage;
import com.toda.api.TODASERVERSPRINGBOOT.entities.PostText;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreatePost;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmParams;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmTokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.KafkaProducerProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractFcmService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("postService")
@RequiredArgsConstructor
public class PostService extends AbstractFcmService implements BaseService {
    private final UserDiaryRepository userDiaryRepository;
    private final PostRepository postRepository;
    private final PostTextRepository postTextRepository;
    private final PostImageRepository postImageRepository;
    private final UserLogRepository userLogRepository;

    private final TokenProvider tokenProvider;
    private final KafkaProducerProvider kafkaProducerProvider;
    private final FcmTokenProvider fcmTokenProvider;

    @Transactional
    public Post addPost(long userID, CreatePost createPost){
        int status = getStatus(createPost.getBackground(), createPost.getMood(), () -> {});

        Post post = new Post();
        post.setUserID(userID);
        post.setDiaryID(createPost.getDiary());
        post.setTitle(createPost.getTitle());
        post.setStatus(status);
        post.setCreateAt(toLocalDateTime(createPost.getDate()));
        Post newPost = postRepository.save(post);
        addPostText(newPost.getPostID(),createPost);

        return post;
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

    @Transactional
    public void setFcmAndLog(UserData sendUserData, Post post, int type){
        // 발송 대상 : 다이어리에 존재하면서 탈퇴하거나 초대받지 않은 유저들 제외
        Map<Long,String> fcmReceiveUserMap = getFcmReceiveUserMap(
                (userDiary,map)-> !map.containsKey(userDiary.getUserID()),
                (userDiary,map)-> map.put(
                        userDiary.getUserID(),
                        userDiary.getUser().getUserName()
                ),
                userDiaryRepository.findByDiaryIDAndStatusNot(post.getDiaryID(),999)
        );

        setKafkaTopicFcm(
                (userID, userName) -> {
                    // 발송 조건 : 상대방 유저가 다이어리에 존재할 경우
                    return getUserDiaryStatus(userID,post.getDiaryID()) == 100;
                },
                // 조건 만족 시 FCM 발송
                (userID, userName) -> {
                    addUserLog(userLogRepository,userID,sendUserData.getUserID(),post.getPostID(),type,100);
                    return fcmTokenProvider.getSingleUserFcmList(userID);
                },
                FcmDto.builder()
                        .title(getFcmTitle())
                        .body(getFcmBody(sendUserData.getUserName(), sendUserData.getUserCode(), "", type))
                        .typeNum(type)
                        .dataID(post.getPostID())
                        .map(fcmReceiveUserMap)
                        .provider(kafkaProducerProvider)
                        .build()
        );
    }





    public long getUserID(String token){return getUserID(token, tokenProvider);}
    public UserData getSendUserData(String token){return tokenProvider.decodeToken(token);}
    public int getUserDiaryStatus(long userID, long diaryID){return getUserDiaryStatus(userID, diaryID, userDiaryRepository);}
}
