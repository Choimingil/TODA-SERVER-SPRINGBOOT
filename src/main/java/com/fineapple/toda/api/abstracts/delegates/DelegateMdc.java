package com.fineapple.toda.api.abstracts.delegates;

import com.fineapple.toda.api.entities.User;
import com.fineapple.toda.api.abstracts.interfaces.BaseMdc;
import com.fineapple.toda.api.enums.LogFields;
import com.fineapple.toda.api.enums.TokenFields;
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
    public void setMdc(User user, String profile) {
        MDC.put(TokenFields.USER_ID.value, String.valueOf(user.getUserID()));
        MDC.put(TokenFields.USER_CODE.value, user.getUserCode());
        MDC.put(TokenFields.EMAIL.value, user.getEmail());
        MDC.put(TokenFields.PASSWORD.value, user.getPassword());
        MDC.put(TokenFields.USER_NAME.value, user.getUserName());
        MDC.put(TokenFields.APP_PASSWORD.value, String.valueOf(user.getAppPassword()));
        MDC.put(TokenFields.CREATE_AT.value, user.getCreateAt().toString());
        MDC.put(TokenFields.PROFILE.value, profile);
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
