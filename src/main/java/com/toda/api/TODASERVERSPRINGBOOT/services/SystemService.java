package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.repositories.SystemRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("systemService")
@RequiredArgsConstructor
public class SystemService extends AbstractService implements BaseService {
    private final SystemRepository systemRepository;

    @Transactional
    public boolean isValidEmail(String email){
        if(!systemRepository.isExistEmail(email)) return true;
        else throw new ValidationException(404,"이미 존재하는 이메일입니다.");
    }
}
