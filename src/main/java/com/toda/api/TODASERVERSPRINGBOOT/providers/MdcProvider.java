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

    public void setMdc(HttpServletRequest request){
        for(MdcKeys keys : mdcKeys) keys.add(request);
        getMdcLogs();
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");
        MDC.put("request_body",bindingResult.getModel().toString());
        logger.info("request_body : " + MDC.get("request_body"));
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
