package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.CreateUser;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserImage;
import com.toda.api.TODASERVERSPRINGBOOT.providers.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserImageRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Component("userService")
@RequiredArgsConstructor
public class UserService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final RedisProvider redisProvider;

    @Value("${toda.url.userImage}")
    private String defaultProfile;

    public String createUserCode(){
        String code = generateRandomString();
        while(userRepository.existsByUserCodeAndAppPasswordNot(code,99999)){
            code = generateRandomString();
        }
        return code;
    }

    @Transactional
    public long createUser(CreateUser createUser, String code){
        User user = new User();
        user.setEmail(createUser.getEmail());
        user.setPassword(createUser.getPassword());
        user.setUserName(createUser.getName());
        user.setUserCode(code);
        User newUser = userRepository.save(user);
        return newUser.getUserID();
    }

    @Transactional
    public void createUserImage(long userID){
        UserImage userImage = new UserImage();
        userImage.setUserID(userID);
        userImage.setUrl(defaultProfile);
        userImageRepository.save(userImage);
    }

    @Transactional
    public void deleteUser(long userID){
        userRepository.deleteUser(userID);
    }

    @Transactional
    public void updateName(long userID, String name){
        userRepository.updateName(name, userID);
    }

    @Transactional
    public void updatePassword(long userID, String password){
        if(userRepository.existsByUserIDAndPasswordAndAppPasswordNot(userID, password, 99999))
            throw new WrongArgException(WrongArgException.of.SAME_PASSWORD_EXCEPTION);

        User user = userRepository.findByUserID(userID);
        if(!userRepository.existsByEmailAndAppPasswordNot(user.getEmail(),99999))
            throw new WrongArgException(WrongArgException.of.NOT_TODA_USER_EXCEPTION);

        user.setPassword(password);
        userRepository.save(user);
        redisProvider.resetUserInfo(user);
    }

    @Transactional
    public void updateProfile(long userID, String url){
        userImageRepository.deleteImage(userID);
        UserImage userImage = new UserImage();
        userImage.setUserID(userID);
        userImage.setUrl(url);
        userImageRepository.save(userImage);
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
