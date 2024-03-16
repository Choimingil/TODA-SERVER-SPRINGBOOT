package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.entities.User;
import com.fineapple.toda.api.entities.UserImage;
import com.fineapple.toda.api.entities.UserSticker;
import com.fineapple.toda.api.entities.mappings.UserLogDetail;
import com.fineapple.toda.api.entities.mappings.UserStickerDetail;
import com.fineapple.toda.api.models.bodies.CreateUser;
import com.fineapple.toda.api.models.protobuffers.JmsMailProto;
import com.fineapple.toda.api.repositories.UserImageRepository;
import com.fineapple.toda.api.repositories.UserLogRepository;
import com.fineapple.toda.api.repositories.UserRepository;
import com.fineapple.toda.api.repositories.UserStickerRepository;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import com.fineapple.toda.api.exceptions.WrongAccessException;
import com.fineapple.toda.api.exceptions.WrongArgException;
import com.fineapple.toda.api.models.responses.get.UserLogResponse;
import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component("userService")
public class UserService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserStickerRepository userStickerRepository;
    private final UserLogRepository userLogRepository;

    public UserService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            UserRepository userRepository,
            UserImageRepository userImageRepository,
            UserStickerRepository userStickerRepository,
            UserLogRepository userLogRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
        this.userStickerRepository = userStickerRepository;
        this.userLogRepository = userLogRepository;
    }

    @Transactional
    public long createUser(CreateUser createUser){
        User user = new User();
        user.setEmail(createUser.getEmail());
        user.setPassword(createUser.getPassword());
        user.setUserName(createUser.getName());
        user.setUserCode(createUserCode());
        User newUser = userRepository.save(user);
        return newUser.getUserID();
    }

    @Transactional
    public void createUserImage(long userID, String profile){
        UserImage userImage = new UserImage();
        userImage.setUserID(userID);
        userImage.setUrl(profile);
        userImageRepository.save(userImage);
    }

    @Transactional
    public void setUserSticker(long userID, List<UserStickerDetail> haveStickerList){
        Set<Long> check = haveStickerList==null ? new HashSet<>() : getObtainBasicStickers(haveStickerList);
        List<UserSticker> addList = new ArrayList<>();
        for(long basicStickerID : basicStickers){
            if(!check.contains(basicStickerID)){
                UserSticker userSticker = new UserSticker();
                userSticker.setUserID(userID);
                userSticker.setStickerPackID(basicStickerID);
                addList.add(userSticker);
            }
        }
        if(!addList.isEmpty()) userStickerRepository.saveAll(addList);
    }

    @Transactional
    public void deleteUser(String token){
        UserDetail userDetail = getUserInfo(token);
        userRepository.deleteUser(userDetail.getUser().getUserID());
        deleteUserInfo(userDetail.getUser().getEmail());
    }

    @Transactional
    public void updateName(String token, String name){
        UserDetail userDetail = getUserInfo(token);
        User user = userDetail.getUser();
        user.setUserName(name);
        userRepository.save(user);
        updateUserRedis(user, userDetail.getProfile());
    }

    @Transactional
    public User updatePassword(String token, String password){
        UserDetail userDetail = getUserInfo(token);
        User user = userDetail.getUser();

        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        updateUserRedis(user, userDetail.getProfile());
        return user;
    }

    @Transactional
    public UserDetail updateAppPassword(String token, int appPassword){
        UserDetail userDetail = getUserInfo(token);
        User user = userDetail.getUser();
        user.setAppPassword(appPassword);
        userRepository.save(user);
        updateUserRedis(user, userDetail.getProfile());
        return userDetail;
    }

    @Transactional
    public void updateProfile(String token, String profile){
        UserDetail userDetail = getUserInfo(token);
        long userID = userDetail.getUser().getUserID();

        userImageRepository.deleteImage(userID);
        createUserImage(userID,profile);
        updateUserRedis(userDetail.getUser(), profile);
    }

    public UserDetail getUserInfoWithUserCode(String userCode){
        return userRepository.getUserDetailByUserCode(userCode);
    }

    @Transactional
    public void updateTempPassword(String email) {
        UserDetail userDetail = getUserInfo(email);
        User user = userDetail.getUser();
        String password = createUserCode();

        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        deleteUserInfo(email);
        sendTempPassword(email,password);
    }

    public List<UserLogResponse> getUserLog(long userID, int page){
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<UserLogDetail> userLogList = userLogRepository.getUserLogs(userID,pageable);

        return userLogList.stream()
                .map(userLogDetail -> UserLogResponse.builder()
                        .type(userLogDetail.getUserLog().getType())
                        .id(userLogDetail.getUserLog().getTypeID())
                        .name(userLogDetail.getUserLog().getUser().getUserName())
                        .selfie(userLogDetail.getSelfie())
                        .image(userLogDetail.getImage())
                        .date(getDateString(userLogDetail.getUserLog().getUpdateAt()))
                        .isReplied(userLogDetail.getIsReplied())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private String createUserCode(){
        String code = generateRandomString();
        while(userRepository.existsByUserCodeAndAppPasswordNot(code,99999)){
            code = generateRandomString();
        }
        return code;
    }

    private String generateRandomString() {
        int length = 9;
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i=0;i<length;i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private void sendTempPassword(String email, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("임시 비밀번호를 발급했어요! 이 비밀번호로 로그인하시고 마이페이지 -> 비밀번호 변경 에 들어가셔서 비밀번호를 변경해주세요!\n\n").append(password);
        String subject = "TODA에서 편지왔어요 :)";

        JmsMailProto.JmsMailRequest params = JmsMailProto.JmsMailRequest.newBuilder()
                .setTo(email)
                .setSubject(subject)
                .setText(sb.toString())
                .build();

        try{
            sendJmsMessage("mail", params).get();
        }
        catch (InterruptedException | ExecutionException e){
            throw new WrongAccessException(WrongAccessException.of.SEND_MAIL_EXCEPTION);
        }
    }

    @Transactional
    private Set<Long> getObtainBasicStickers(List<UserStickerDetail> haveStickerList){
        Set<Long> set = new HashSet<>();
        for(UserStickerDetail sticker : haveStickerList){
            long stickerPackID = sticker.getStickerPackID();
            if(set.contains(stickerPackID)){
                userStickerRepository.deleteById(sticker.getUserStickerID());
                break;
            }

            for(long basicStickerID : basicStickers){
                if(stickerPackID == basicStickerID){
                    set.add(stickerPackID);
                    break;
                }
            }
        }
        return set;
    }
}
