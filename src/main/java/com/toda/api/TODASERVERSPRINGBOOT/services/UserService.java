package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserImage;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserSticker;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateUser;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserLogDetail;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaMailProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.*;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component("userService")
@RequiredArgsConstructor
public class UserService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserStickerRepository userStickerRepository;
    private final UserLogRepository userLogRepository;
    private final UserProvider userProvider;
    private final TokenProvider tokenProvider;
    private final KafkaProducerProvider kafkaProducerProvider;

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
        UserData userData = userProvider.getUserInfo(token);
        userRepository.deleteUser(userData.getUserID());
        userProvider.deleteUserInfo(userData.getEmail());
    }

    @Transactional
    public void updateName(String token, String name){
        UserData userData = userProvider.getUserInfo(token);
        User user = userData.toUser();
        user.setUserName(name);
        userRepository.save(user);
        userProvider.setUserInfo(userData);
    }

    @Transactional
    public void updatePassword(String token, String password){
        UserData userData = userProvider.getUserInfo(token);
        User user = userData.toUser();

        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        userProvider.setUserInfo(userData);
    }

    @Transactional
    public UserData updateAppPassword(String token, int appPassword){
        UserData userData = userProvider.getUserInfo(token);
        User user = userData.toUser();
        user.setAppPassword(appPassword);
        userRepository.save(user);
        userProvider.setUserInfo(userData);
        return userData;
    }

    @Transactional
    public void updateProfile(String token, String profile){
        UserData userData = userProvider.getUserInfo(token);
        long userID = userData.getUserID();

        userImageRepository.deleteImage(userID);
//        userImageRepository.deleteByUserID(userID);

        createUserImage(userID,profile);
        userProvider.setUserInfo(userData);
    }

    public UserData getUserInfo(String token){
        return userProvider.getUserInfo(token);
    }

    public UserInfoDetail getUserInfoWithUserCode(String userCode){
        return userRepository.getUserDataByUserCode(userCode);
    }

    @Transactional
    public void updateTempPassword(String email) {
        UserData userData = userProvider.getUserInfo(email);
        User user = userData.toUser();
        String password = createUserCode();

        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        userProvider.deleteUserInfo(email);
        sendTempPassword(email,password);
    }

    public List<UserLogDetail> getUserLog(String token, int page){
        long userID = tokenProvider.getUserID(token);
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        return userLogRepository.getUserLogs(userID,pageable);
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

        KafkaMailProto.KafkaMailRequest params = KafkaMailProto.KafkaMailRequest.newBuilder()
                .setTo(email)
                .setSubject(subject)
                .setText(sb.toString())
                .build();

        try{
            kafkaProducerProvider.getKafkaProducer("mail", params).get();
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
