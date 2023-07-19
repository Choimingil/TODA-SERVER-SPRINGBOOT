package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.MdcKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.EnumSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public final class MdcProvider extends AbstractProvider implements BaseProvider {
    private final Set<MdcKeys> mdcKeys = EnumSet.allOf(MdcKeys.class);
    private final Set<MdcKeys> mandatoryKeys = EnumSet.of(
            MdcKeys.REQUEST_ID,
            MdcKeys.REQUEST_CONTEXT_PATH,
            MdcKeys.REQUEST_URL,
            MdcKeys.REQUEST_METHOD,
            MdcKeys.REQUEST_TIME,
            MdcKeys.REQUEST_IP
    );

    @Override
    public void afterPropertiesSet() {
        mdcKeys.remove(MdcKeys.REQUEST_BODY);
    }

    public boolean isMdcSet(){
        return mandatoryKeys.stream()
                .allMatch(keys -> keys.get() != null);
    }

    public void setMdc(HttpServletRequest request){
        for(MdcKeys keys : mdcKeys) keys.add(request,logger);
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        mdcKeys.add(MdcKeys.REQUEST_BODY);
        MdcKeys.REQUEST_BODY.add(bindingResult, logger);
    }

    public void removeMdc(){
        for(MdcKeys keys : mdcKeys) keys.remove(logger);
    }
}
