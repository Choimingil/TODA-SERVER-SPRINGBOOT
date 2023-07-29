package com.toda.api.TODASERVERSPRINGBOOT.services.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService implements BaseService{
    protected final Logger logger = LoggerFactory.getLogger(AbstractService.class);
}
