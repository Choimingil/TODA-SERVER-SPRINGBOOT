package com.toda.api.TODASERVERSPRINGBOOT.controllers.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements BaseController {
    protected final Logger logger = LoggerFactory.getLogger(AbstractController.class);
}
