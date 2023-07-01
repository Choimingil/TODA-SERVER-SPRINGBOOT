package com.toda.api.TODASERVERSPRINGBOOT.controllers.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractController implements BaseController {
    protected final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public boolean isFail(Map<String,?> map){
        if(!map.containsKey("isSuccess")) return false;
        return !((boolean) map.get("isSuccess"));
    }
}
