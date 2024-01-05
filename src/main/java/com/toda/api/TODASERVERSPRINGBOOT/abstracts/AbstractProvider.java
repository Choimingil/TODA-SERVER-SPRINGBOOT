package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractProvider implements BaseProvider {
    protected final Logger logger = LoggerFactory.getLogger(AbstractProvider.class);


}
