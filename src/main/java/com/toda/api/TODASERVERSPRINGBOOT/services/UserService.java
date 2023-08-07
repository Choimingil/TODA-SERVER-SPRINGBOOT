package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateUser;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserLogDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserStickerDetail;
import com.toda.api.TODASERVERSPRINGBOOT.providers.*;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component("userService")
@RequiredArgsConstructor
public class UserService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserStickerRepository userStickerRepository;
    private final UserLogRepository userLogRepository;
    private final RedisProvider redisProvider;
    private final TokenProvider tokenProvider;
    private final DateProvider dateProvider;
    private final MailProvider mailProvider;

    @Value("${toda.sticker.basicNum}")
    private String basicStickerNum;

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
    public void setBasicSticker(long userID){
        List<UserSticker> addList = new ArrayList<>();
        for(long basicStickerID : StickerProvider.BASIC_STICKER_SET){
            UserSticker userSticker = new UserSticker();
            userSticker.setUserID(userID);
            userSticker.setStickerPackID(basicStickerID);
            addList.add(userSticker);
        }
        userStickerRepository.saveAll(addList);
    }

    @Transactional
    public void resetBasicSticker(long userID){
        List<UserSticker> haveStickerList = userStickerRepository.findByUserID(userID);

        Set<Long> check = new HashSet<>();
        for(UserSticker sticker : haveStickerList){
            long stickerPackID = sticker.getStickerPackID();
            if(check.contains(stickerPackID)){
                userStickerRepository.deleteById(sticker.getUserStickerID());
                break;
            }

            for(long basicStickerID : StickerProvider.BASIC_STICKER_SET){
                if(stickerPackID == basicStickerID){
                    check.add(stickerPackID);
                    break;
                }
            }
        }

        List<UserSticker> addList = new ArrayList<>();
        for(long basicStickerID : StickerProvider.BASIC_STICKER_SET){
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
        UserData userData = redisProvider.getUserInfo(token);
        userRepository.deleteUser(userData.getUserID());
        redisProvider.deleteUserInfo(userData.getEmail());
    }

    @Transactional
    public void updateName(String token, String name){
        UserData userData = redisProvider.getUserInfo(token);
        User user = userData.toUser();
        user.setUserName(name);
        userRepository.save(user);
        redisProvider.setUserInfo(userData);
    }

    @Transactional
    public void updatePassword(String token, String password){
        UserData userData = redisProvider.getUserInfo(token);
        User user = userData.toUser();

        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        redisProvider.setUserInfo(userData);
    }

    @Transactional
    public void updateTempPassword(String email) {
        UserData userData = redisProvider.getUserInfo(email);
        User user = userData.toUser();
        String password = createUserCode();

        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        redisProvider.deleteUserInfo(email);

        try{
            mailProvider.sendTempPassword(email,password).get();
        }
        catch (ExecutionException | InterruptedException e){
            throw new WrongAccessException(WrongAccessException.of.SEND_MAIL_EXCEPTION);
        }
    }

    @Transactional
    public void updateProfile(String token, String profile){
        UserData userData = redisProvider.getUserInfo(token);
        long userID = userData.getUserID();

        userImageRepository.deleteImage(userID);
//        userImageRepository.deleteByUserID(userID);

        createUserImage(userID,profile);
        redisProvider.setUserInfo(userData);
    }

    @Transactional
    public UserData updateAppPassword(String token, int appPassword){
        UserData userData = redisProvider.getUserInfo(token);
        User user = userData.toUser();
        user.setAppPassword(appPassword);
        userRepository.save(user);
        redisProvider.setUserInfo(userData);
        return userData;
    }

    public Map<String,?> getUserInfo(String token){
        UserData userData = tokenProvider.decodeToken(token);

        Map<String,Object> map = new HashMap<>();
        map.put("userID",userData.getUserID());
        map.put("userCode",userData.getUserCode());
        map.put("appPW",userData.getAppPassword());
        map.put("email",userData.getEmail());
        map.put("name",userData.getUserName());
        map.put("birth",userData.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.put("selfie",userData.getProfile());
        return map;
    }

    public Map<String,?> getUserInfoWithUserCode(String userCode){
        User user = userRepository.findByUserCode(userCode);
        UserImage userImage = userImageRepository.findByUserIDAndStatusNot(user.getUserID(),0);

        Map<String,Object> map = new HashMap<>();
        map.put("userID",user.getUserID());
        map.put("userCode",user.getUserCode());
        map.put("appPW",user.getAppPassword());
        map.put("email",user.getEmail());
        map.put("name",user.getUserName());
        map.put("birth",user.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.put("selfie",userImage.getUrl());
        return map;
    }

    public List<UserStickerDetail> getUserStickers(String token, int page){
        UserData userData = tokenProvider.decodeToken(token);
        int start = (page-1)*10;
        Pageable pageable = PageRequest.of(start,10);
        return userStickerRepository.getUserStickers(userData.getUserID(),pageable);
    }

    public List<Map<String,?>> getUserLog(String token, int page){
        UserData userData = tokenProvider.decodeToken(token);
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<UserLogDetail> userLogDetails = userLogRepository.getUserLogs(userData.getUserID(),pageable);

        return userLogDetails.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("type", element.getType());
            map.put("ID", element.getID());
            map.put("name", element.getName());
            map.put("selfie", element.getSelfie());
            map.put("date", dateProvider.getTimeDiff(element.getDate()));
            map.put("isReplied", element.getIsReplied());
            return map;
        }).collect(Collectors.toList());
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
}
