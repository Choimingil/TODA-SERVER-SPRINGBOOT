package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserRepository;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("systemService")
public class SystemService extends AbstractService implements BaseService {
    private final UserRepository userRepository;
    @Value("${toda.ios.version.prev}")
    private String iosVerPrev;
    @Value("${toda.ios.version.curr}")
    private String iosVerCurr;
    @Value("${toda.aos.version.prev}")
    private String aosVerPrev;
    @Value("${toda.aos.version.curr}")
    private String aosVerCurr;

    public SystemService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateJms delegateJms,
            UserRepository userRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateJms);
        this.userRepository = userRepository;
    }

    public boolean isExistEmail(String email){
        return !userRepository.existsByEmailAndAppPasswordNot(email,99999);
    }

    public boolean isMyEmail(long userID, String email){
        return userRepository.existsByUserIDAndEmail(userID, email);
    }

    public boolean isValidIosVersion(String version){
        return version.equals(iosVerPrev) || version.equals(iosVerCurr);
    }

    public boolean isValidAosVersion(String version){
        return version.equals(aosVerPrev) || version.equals(aosVerCurr);
    }
    
    public String readPrivacyTerm() {
        return readTxtFile("privacy.txt");
    }
}
