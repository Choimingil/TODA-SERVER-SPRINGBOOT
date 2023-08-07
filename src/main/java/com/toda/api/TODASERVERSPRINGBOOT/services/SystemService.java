package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component("systemService")
@RequiredArgsConstructor
public class SystemService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Value("${toda.ios.version.prev}")
    private String iosVerPrev;
    @Value("${toda.ios.version.curr}")
    private String iosVerCurr;
    @Value("${toda.aos.version.prev}")
    private String aosVerPrev;
    @Value("${toda.aos.version.curr}")
    private String aosVerCurr;

    public boolean isExistEmail(String email){
        return !userRepository.existsByEmailAndAppPasswordNot(email,99999);
    }

    public boolean isMyEmail(String token, String email){
        long userID = tokenProvider.getUserID(token);
        return userRepository.existsByUserIDAndEmail(userID, email);
    }

    public boolean isValidIosVersion(String version){
        return version.equals(iosVerPrev) || version.equals(iosVerCurr);
    }

    public boolean isValidAosVersion(String version){
        return version.equals(aosVerPrev) || version.equals(aosVerCurr);
    }
    
    public String readTxtFile(String filename) {
        try{
            ClassPathResource resource = new ClassPathResource(filename);
            InputStream inputStream = resource.getInputStream();
            byte[] fileData = FileCopyUtils.copyToByteArray(inputStream);
            return new String(fileData, StandardCharsets.UTF_8);
        }
        catch (IOException e){
            throw new WrongAccessException(WrongAccessException.of.READ_TXT_EXCEPTION);
        }
    }
}
