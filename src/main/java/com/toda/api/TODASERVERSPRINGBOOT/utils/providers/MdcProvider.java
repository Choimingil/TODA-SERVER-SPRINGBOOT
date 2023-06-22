package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.SystemController;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.validation.BindingResult;

public class MdcProvider {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    // Singleton Pattern
    private static MdcProvider mdcProvider = null;
    public static MdcProvider getInstance(){
        if(mdcProvider == null){
            mdcProvider = new MdcProvider();
        }
        return mdcProvider;
    }

    public void setBody(BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new ValidationException(404,"잘못된 요청값입니다.");
        MDC.put("request_body",bindingResult.getModel().toString());
        logger.info("request_body : " + MDC.get("request_body"));
    }
}
