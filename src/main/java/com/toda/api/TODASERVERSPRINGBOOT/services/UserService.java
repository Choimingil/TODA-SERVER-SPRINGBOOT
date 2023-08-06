package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateUser;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserLogDetail;
import com.toda.api.TODASERVERSPRINGBOOT.providers.DateProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.*;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component("userService")
@RequiredArgsConstructor
public class UserService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserStickerRepository userStickerRepository;
    private final UserLogRepository userLogRepository;
    private final PostImageRepository postImageRepository;
    private final RedisProvider redisProvider;
    private final TokenProvider tokenProvider;
    private final DateProvider dateProvider;

    private String createUserCode(){
        String code = generateRandomString();
        while(userRepository.existsByUserCodeAndAppPasswordNot(code,99999)){
            code = generateRandomString();
        }
        return code;
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
    public void setSticker(long userID){
        List<Long> stickerPackList = Arrays.asList(1L,2L,3L,4L);
        if(!userStickerRepository.existsByUserIDAndStickerPackIDIn(userID,stickerPackList)){
            List<UserSticker> list = new ArrayList<>();
            for(long stickerPackID : stickerPackList){
                UserSticker userSticker = new UserSticker();
                userSticker.setUserID(userID);
                userSticker.setStickerPackID(stickerPackID);
            }
            userStickerRepository.saveAll(list);
        }
    }

    @Transactional
    public void deleteUser(String token){
        User user = tokenProvider.getUserInfo(token);
        userRepository.deleteUser(user.getUserID());
        redisProvider.deleteUserInfo(user);
    }

    @Transactional
    public void updateName(String token, String name){
        User user = tokenProvider.getUserInfo(token);
        user.setUserName(name);
        userRepository.save(user);
        redisProvider.setUserInfo(user, tokenProvider.getUserProfile(token));
    }

    @Transactional
    public void updatePassword(String token, String password){
        User user = tokenProvider.getUserInfo(token);
        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(user.getUserID(), password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        redisProvider.setUserInfo(user, tokenProvider.getUserProfile(token));

    }

    @Transactional
    public void updateProfile(String token, String profile){
        User user = tokenProvider.getUserInfo(token);
        long userID = user.getUserID();
        userImageRepository.deleteImage(userID);
//        userImageRepository.deleteByUserID(userID);
        createUserImage(userID,profile);
        redisProvider.setUserInfo(user, tokenProvider.getUserProfile(token));
    }

    public Map<String,?> getUserInfo(String token){
        User user = tokenProvider.getUserInfo(token);
        String profile = tokenProvider.getUserProfile(token);

        Map<String,Object> map = new HashMap<>();
        map.put("userID",user.getUserID());
        map.put("userCode",user.getUserCode());
        map.put("appPW",user.getAppPassword());
        map.put("email",user.getEmail());
        map.put("name",user.getUserName());
        map.put("birth",user.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.put("selfie",profile);
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

    public List<Map<String,?>> getUserLog(String token, int page){
        User user = tokenProvider.getUserInfo(token);
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<UserLogDetail> userLogDetails = userLogRepository.getUserLogs(user.getUserID(),pageable);

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
