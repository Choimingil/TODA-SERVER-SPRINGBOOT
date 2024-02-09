package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.PostList;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreatePost;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.UpdatePost;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.FcmDto;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.PostImageResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.PostDetailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.get.PostListResponse;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component("postService")
public class PostService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final PostRepository postRepository;
    private final PostTextRepository postTextRepository;
    private final PostImageRepository postImageRepository;
    private final HeartRepository heartRepository;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;

    public PostService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            UserRepository userRepository,
            UserDiaryRepository userDiaryRepository,
            PostRepository postRepository,
            PostTextRepository postTextRepository,
            PostImageRepository postImageRepository,
            HeartRepository heartRepository,
            NotificationRepository notificationRepository,
            CommentRepository commentRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.userRepository = userRepository;
        this.userDiaryRepository = userDiaryRepository;
        this.postRepository = postRepository;
        this.postTextRepository = postTextRepository;
        this.postImageRepository = postImageRepository;
        this.heartRepository = heartRepository;
        this.notificationRepository = notificationRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Post addPost(long userID, CreatePost createPost){
        int status = getStatus(createPost.getBackground(), createPost.getMood(), 100, () -> {});

        Post post = new Post();
        post.setUserID(userID);
        post.setDiaryID(createPost.getDiary());
        post.setTitle(createPost.getTitle());
        post.setStatus(status);
        post.setCreateAt(toLocalDateTime(createPost.getDate()));
        Post newPost = postRepository.save(post);
        addPostText(newPost.getPostID(),createPost);

        return newPost;
    }

    @Transactional
    private void addPostText(long postID, CreatePost createPost){
        int status = getStatus(createPost.getAligned(), createPost.getFont(), 100, () -> {});

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
    public void setFcmAndLog(Map<Long,String> map, UserDetail sendUser, Post post, int type){
        setJmsTopicFcm(
                sendUser.getUser().getUserID(),
                (userID, userName) -> {
                    // 발송 조건 : 상대방 유저가 다이어리에 존재할 경우
                    return getUserDiaryStatus(userID,post.getDiaryID()) == 100;
                },
                // 조건 만족 시 FCM 발송
                (userID, userName) -> {
                    addUserLog(userID,sendUser.getUser().getUserID(),post.getPostID(),type,100);
                    return getUserFcmTokenList(userID, notificationRepository);
                },
                FcmDto.builder()
                        .title(getFcmTitle())
                        .body(getFcmBody(
                                sendUser.getUser().getUserName(),
                                sendUser.getUser().getUserCode(),
                                post.getUser() == null ? userRepository.findByUserID(post.getUserID()).getUserName() : post.getUser().getUserName(),
                                type))
                        .typeNum(type)
                        .dataID(post.getPostID())
                        .map(map)
                        .build()
        );
    }

    @Transactional
    public void deletePost(long postID){
        Post post = postRepository.findByPostID(postID);
        post.setStatus(0);
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(UpdatePost updatePost){
        int status = getStatus(updatePost.getBackground(), updatePost.getMood(), 100, () -> {});

        Post post = postRepository.findByPostID(updatePost.getPost());
        post.setTitle(updatePost.getTitle());
        post.setStatus(status);
        if(!updatePost.getDate().equals("1901-01-01")) post.setCreateAt(toLocalDateTime(updatePost.getDate()));
        postRepository.save(post);
    }

    @Transactional
    public void updatePostText(UpdatePost updatePost){
        int status = getStatus(updatePost.getAligned(), updatePost.getFont(), 100, () -> {});

        List<PostText> postTextList = postTextRepository.findByPostID(updatePost.getPost());
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                postText -> isEdit.get(),
                postText -> {
                    postText.setText(updatePost.getText());
                    postText.setStatus(status);
                    isEdit.set(false);
                },
                postTextList,
                postTextRepository
        );
    }

    @Transactional
    public void deletePostImage(long postID){
        List<PostImage> postImageList = postImageRepository.findByPostIDAndStatusNot(postID,0);
        updateList(
                postImageList,
                postImage -> postImage.setStatus(0),
                postImageRepository
        );
    }

    @Transactional
    public void addHeart(long userID, long postID, int mood){
        Heart heart = new Heart();
        heart.setUserID(userID);
        heart.setPostID(postID);
        heart.setStatus(mood);
        heartRepository.save(heart);
    }

    @Transactional
    public void updateHeart(Heart heart, int status){
        heart.setStatus(status);
        heartRepository.save(heart);
    }

    public List<PostListResponse> getPostList(long userID, long diaryID, int page){
        List<PostListResponse> res = new ArrayList<>();

        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<PostList> postList = postRepository.getPostList(userID, diaryID, pageable);

        for(PostList item : postList){
            int moodCode = item.getPost().getStatus()%100;
            PostListResponse response = PostListResponse.builder()
                    .diaryID(item.getPost().getDiaryID())
                    .postID(item.getPost().getPostID())
                    .name(item.getPost().getUser().getUserName())
                    .title(item.getPost().getTitle())
                    .date(getDateString(item.getPost().getCreateAt()))
                    .icon(moodCode)
                    .mood(getMoodString(moodCode))
                    .isMyLike(item.getIsMyLike()==1)
                    .likeNum(item.getLikeNum())
                    .commentNum(item.getCommentNum())
                    .build();
            res.add(response);
        }

        return res;
    }

    public PostDetailResponse getPostDetail(long userID, long postID){
        PostText postText = postTextRepository.findByPostID(postID).get(0);

        int isMyLike = heartRepository.getIsMyLike(userID,postID);
        int likeNum = heartRepository.countByPostIDAndStatusNot(postID,0);
        int commentNum = commentRepository.countByPostIDAndStatusNot(postID,0);

        List<PostImage> postImageList = postImageRepository.findByPostIDAndStatusNot(postID,0);

        Post post = postText.getPost();
        int moodCode = post.getStatus()%100;
        int backgroundCode = post.getStatus()/100;
        int fontCode = postText.getStatus()%100;
        int alignedCode = postText.getStatus()/100;

        List<PostImageResponse> validImageList = postImageList.stream()
                .map(postImage -> PostImageResponse.builder()
                        .imageID(postImage.getPostImageID())
                        .url(postImage.getUrl())
                        .build())
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .isMyPost(post.getUserID() == userID)
                .diaryID(post.getDiaryID())
                .postID(postID)
                .name(post.getUser().getUserName())
                .date(getDateString(post.getCreateAt()))
                .dateFull(toStringDateFullTime(post.getCreateAt()))
                .title(post.getTitle())
                .text(postText.getText())
                .icon(moodCode)
                .mood(getMoodString(moodCode))
                .background(backgroundCode)
                .font(fontCode)
                .aligned(alignedCode)
                .isMyLike(isMyLike==1)
                .likeNum(likeNum)
                .commentNum(commentNum)
                .image(validImageList)
                .build();
    }










    /**
     * 유효한 좋아요만 리턴하고 나머지는 삭제
     * @return
     */
    public Heart getValidHeart(List<Heart> heartList){
        Queue<Heart> queue = new ArrayDeque<>();
        AtomicBoolean isEdit = new AtomicBoolean(true);
        updateListAndDelete(
                heart -> isEdit.get(),
                heart -> {
                    queue.add(heart);
                    isEdit.set(false);
                },
                heartList,
                heartRepository
        );
        return queue.poll();
    }

    /**
     * 게시글 작성 시 FCM 발송받을 유저 데이터 getter
     * 발송 대상 : 다이어리에 존재하면서 탈퇴하거나 초대받지 않은 유저들 제외
     * 이 때 자기 자신은 알림 대상에서 제외
     * @param userID,diaryID
     * @return
     */
    public Map<Long,String> getFcmAddPostUserMap(long userID, long diaryID){
        return getFcmReceiveUserMap(
                (userDiary,map)-> !map.containsKey(userDiary.getUserID()) && userID != userDiary.getUserID(),
                (userDiary,map)-> map.put(
                        userDiary.getUserID(),
                        userDiary.getUser().getUserName()
                ),
                userDiaryRepository.findByDiaryIDAndStatusNot(diaryID,999)
        );
    }

    /**
     * 좋아요 추가 시 FCM 발송받을 유저 데이터 getter
     * 발송 대상 : 게시글 주인
     * 이 때 자기 자신은 알림 대상에서 제외
     * @param userID,user
     * @return
     */
    public Map<Long,String> getFcmAddHeartUserMap(long userID, User user){
        Map<Long,String> res = new HashMap<>();
        if(user.getUserID() != userID) res.put(user.getUserID(), user.getUserName());
        return res;
    }

    /**
     * 감정 코드를 스트링으로 변환
     * @param moodCode
     * @return
     */
    private String getMoodString(int moodCode){
        return switch (moodCode) {
            case 1 -> "기분 등록 없음";
            case 2 -> "행복해요";
            case 3 -> "슬퍼요";
            case 4 -> "화나요";
            case 5 -> "우울해요";
            case 6 -> "설레요";
            case 7 -> "멍때려요";
            default -> "잘못된 감정";
        };
    }



    public Post getPostByID(long postID){return postRepository.findByPostID(postID);}
    public List<Heart> getHeartList(long userID, long postID){return heartRepository.findByUserIDAndPostIDOrderByCreateAtDesc(userID,postID);}
}
