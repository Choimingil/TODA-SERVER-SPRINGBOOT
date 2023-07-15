package com.toda.api.TODASERVERSPRINGBOOT.handlers.base;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractExceptionHandler implements BaseExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(AbstractExceptionHandler.class);

    /**
     * Exception 종류 별 실패를 MDC 값을 기준으로 리턴해주는 골격 메소드
     * @param e
     * @param elementCode
     * @param errorMessage
     * @return
     */
    @Override
    public Map<String,?> getResponse(Exception e, int elementCode, String errorMessage){
        logger.error(e.getMessage());
//        getSlackProvider().doSlack(e);
        return new FailResponse.Builder(elementCode, errorMessage)
                .build()
                .getResponse();
    }

    /**
     * Exception 종류 별 실패를 HttpServletRequest 값을 기준으로 리턴해주는 골격 메소드
     * @param request
     * @param e
     * @param elementCode
     * @param errorMessage
     * @return
     */
    @Override
    public Map<String,?> getResponse(HttpServletRequest request, Exception e, int elementCode, String errorMessage){
        logger.error(e.getMessage());
//        getSlackProvider().doSlack(request,e);
        return new FailResponse.Builder(elementCode, errorMessage)
                .build()
                .getResponse();
    }

    /**
     * 예상하지 못한 에러 발생 시 Error Message 생성 메소드 구현
     * @param e
     * @return
     */
    @Override
    public String getErrorMsg(Exception e){
        StringBuilder sb = new StringBuilder();
        sb.append("exception type :");
        sb.append(e.getClass());
        sb.append(" \nexception text : ");
        sb.append(e.getMessage());
        return sb.toString();
    }
}
