package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.repositories.SystemRepository;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("systemService")
@RequiredArgsConstructor
public final class SystemService {
    private final SystemRepository systemRepository;
    public boolean isValidEmail(String email){
        if(!systemRepository.isExistEmail(email)) return true;
        else throw new ValidationException(404,"이미 존재하는 이메일입니다.");
    }
}
