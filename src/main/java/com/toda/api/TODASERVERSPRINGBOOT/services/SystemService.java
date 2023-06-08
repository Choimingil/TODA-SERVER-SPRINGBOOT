package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.models.dto.requests.ValidateEmailDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.DefaultResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.SystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("systemService")
@RequiredArgsConstructor
public class SystemService {
    private final SystemRepository systemRepository;
    public DefaultResponseDTO validateEmail(ValidateEmailDTO validateEmailDTO){
        String email = validateEmailDTO.getEmail();
        if(!systemRepository.isExistEmail(email)) return new DefaultResponseDTO(100,"사용 가능한 이메일입니다.");
        else return new DefaultResponseDTO(404,"이미 존재하는 이메일입니다.");
    }
}
