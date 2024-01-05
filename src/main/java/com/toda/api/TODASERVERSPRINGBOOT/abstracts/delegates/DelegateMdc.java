package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseMdc;
import com.toda.api.TODASERVERSPRINGBOOT.enums.LogFields;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public final class DelegateMdc implements BaseMdc {
    private final Logger logger = LoggerFactory.getLogger(DelegateMdc.class);
    private final Set<LogFields> logSet = EnumSet.allOf(LogFields.class);
    private final Set<LogFields> mandatoryKeys = EnumSet.of(
            LogFields.REQUEST_ID,
            LogFields.REQUEST_CONTEXT_PATH,
            LogFields.REQUEST_URL,
            LogFields.REQUEST_METHOD,
            LogFields.REQUEST_TIME,
            LogFields.REQUEST_IP
    );

    @Override
    public void setMdc(UserData userData) {
        MDC.put(TokenFields.USER_ID.value, String.valueOf(userData.getUserID()));
        MDC.put(TokenFields.USER_CODE.value, userData.getUserCode());
        MDC.put(TokenFields.EMAIL.value, userData.getEmail());
        MDC.put(TokenFields.PASSWORD.value, userData.getPassword());
        MDC.put(TokenFields.USER_NAME.value, userData.getUserName());
        MDC.put(TokenFields.APP_PASSWORD.value, String.valueOf(userData.getAppPassword()));
        MDC.put(TokenFields.CREATE_AT.value, userData.getCreateAt().toString());
        MDC.put(TokenFields.PROFILE.value, userData.getProfile());
    }

    @Override
    public void removeMdc() {
        MDC.remove(TokenFields.USER_ID.value);
        MDC.remove(TokenFields.USER_CODE.value);
        MDC.remove(TokenFields.EMAIL.value);
        MDC.remove(TokenFields.PASSWORD.value);
        MDC.remove(TokenFields.USER_NAME.value);
        MDC.remove(TokenFields.APP_PASSWORD.value);
        MDC.remove(TokenFields.CREATE_AT.value);
        MDC.remove(TokenFields.PROFILE.value);
    }

    @Override
    public boolean isMdcSet() {
        return mandatoryKeys.stream().allMatch(keys -> keys.get() != null);
    }

    @Override
    public void setLogSet(HttpServletRequest request) {
        for(LogFields keys : logSet) keys.add(request,logger);
    }
}
