package com.toda.api.TODASERVERSPRINGBOOT.providers.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractProvider implements BaseProvider, InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(AbstractProvider.class);


}
