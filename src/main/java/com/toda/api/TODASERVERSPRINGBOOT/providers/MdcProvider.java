package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.MdcKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
public final class MdcProvider extends AbstractProvider implements BaseProvider {
    private final EnumSet<MdcKeys> mdcKeys = EnumSet.allOf(MdcKeys.class);
    private final EnumSet<MdcKeys> mandatoryKeys = EnumSet.of(
            MdcKeys.REQUEST_ID,
            MdcKeys.REQUEST_CONTEXT_PATH,
            MdcKeys.REQUEST_URL,
            MdcKeys.REQUEST_METHOD,
            MdcKeys.REQUEST_TIME,
            MdcKeys.REQUEST_IP
    );

    public boolean isMdcSet(){
        for(MdcKeys keys : mandatoryKeys){
            if(keys.get() == null) return false;
        }
        return true;
    }

    public boolean isMdcBodyExist(){
        return MdcKeys.REQUEST_BODY.get() != null;
    }

    public void setMdc(HttpServletRequest request){
        for(MdcKeys keys : mdcKeys) keys.add(request);
        getMdcLogs();
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");
        MdcKeys.REQUEST_BODY.add(bindingResult);
        MdcKeys.REQUEST_BODY.log();
    }

    public void removeMdc(){
        for(MdcKeys keys : mdcKeys) keys.remove();
        getMdcLogs();
    }

    private void getMdcLogs(){
        for(MdcKeys keys : mdcKeys) keys.log();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
