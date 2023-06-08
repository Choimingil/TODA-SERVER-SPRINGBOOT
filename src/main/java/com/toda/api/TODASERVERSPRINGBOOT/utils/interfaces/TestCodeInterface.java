package com.toda.api.TODASERVERSPRINGBOOT.utils.interfaces;

import org.springframework.test.web.servlet.MvcResult;

public interface TestCodeInterface {
    public <T> void doTest(MvcResult result, Class<T> c, T expected) throws Exception;

    public <T> void getResult(MvcResult result) throws Exception;


}
