package com.toda.api.TODASERVERSPRINGBOOT.handlers.base;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

//    /**
//     * Filter level에서 발생한 오류 slack 전송
//     * @param slackApi
//     * @param slackAttachment
//     * @param slackMessage
//     * @param request
//     * @param exception
//     */
//    private void doSlack(
//            SlackApi slackApi,
//            SlackAttachment slackAttachment,
//            SlackMessage slackMessage,
//            HttpServletRequest request,
//            Exception exception
//    ) {
//        slackAttachment.setTitleLink(request.getContextPath());
//        slackAttachment.setFields(
//                slackKeysEnumSet.stream()
//                .map(keys -> keys.addRequest(request))
//                .collect(Collectors.toList())
//        );
//
//        try {
//            sendSlack(slackApi,slackAttachment,slackMessage,exception).get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new WrongAccessException(WrongAccessException.of.SEND_SLACK_EXCEPTION);
//        }
//    }
//
//    /**
//     * ControllerAdvice level에서 발생한 오류 slack 전송
//     * @param slackApi
//     * @param slackAttachment
//     * @param slackMessage
//     * @param exception
//     */
//    private void doSlack(
//            SlackApi slackApi,
//            SlackAttachment slackAttachment,
//            SlackMessage slackMessage,
//            Exception exception
//    ) {
//        slackKeysEnumSet.add(SlackKeys.REQUEST_BODY);
//        slackAttachment.setTitleLink(MDC.get("request_context_path"));
//        slackAttachment.setFields(
//                slackKeysEnumSet.stream()
//                .map(SlackKeys::addMdc)
//                .collect(Collectors.toList())
//        );
//
//
//        try {
//            sendSlack(slackApi,slackAttachment,slackMessage,exception).get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new WrongAccessException(WrongAccessException.of.SEND_SLACK_EXCEPTION);
//        }
//    }
//
//    /**
//     * 비동기로 slack 전송
//     * @param slackApi
//     * @param slackAttachment
//     * @param slackMessage
//     * @param e
//     * @return
//     */
//    @Async
//    private Future<Void> sendSlack(
//            SlackApi slackApi,
//            SlackAttachment slackAttachment,
//            SlackMessage slackMessage,
//            Exception e
//    ){
//        slackAttachment.setText(Arrays.toString(e.getStackTrace()));
//        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
//        slackApi.call(slackMessage);
//        return CompletableFuture.completedFuture(null);
//    }
}
