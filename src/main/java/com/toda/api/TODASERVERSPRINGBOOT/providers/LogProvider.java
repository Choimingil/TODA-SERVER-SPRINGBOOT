package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.enums.LogFields;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.EnumSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public final class LogProvider extends AbstractProvider implements BaseProvider {
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
    public void afterPropertiesSet() {
        logSet.remove(LogFields.REQUEST_BODY);
    }

    public boolean isMdcSet(){
        return mandatoryKeys.stream()
                .allMatch(keys -> keys.get() != null);
    }

    public void setMdc(HttpServletRequest request){
        for(LogFields keys : logSet) keys.add(request,logger);
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        logSet.add(LogFields.REQUEST_BODY);
        LogFields.REQUEST_BODY.add(bindingResult, logger);
    }

    public void removeMdc(){
        for(LogFields keys : logSet) keys.remove(logger);
    }
}
