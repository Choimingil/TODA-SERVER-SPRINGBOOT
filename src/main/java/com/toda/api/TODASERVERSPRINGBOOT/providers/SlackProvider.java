package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.SlackKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.*;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SlackProvider extends AbstractProvider implements BaseProvider {
    private final MdcProvider mdcProvider;
    private final SlackApi slackApi;
    private final SlackAttachment slackAttachment;
    private final SlackMessage slackMessage;
    private final EnumSet<SlackKeys> slackKeysEnumSet = EnumSet.allOf(SlackKeys.class);

    @Override
    public void afterPropertiesSet() {
        slackKeysEnumSet.remove(SlackKeys.REQUEST_BODY);
    }

    public void sendSlackWithNoMdc(HttpServletRequest request, Exception e) {
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setFields(getSlackFieldsWithRequest(request));
        send(e);
    }

    @Async
    public void sendSlackWithMdc(Exception e) {
        slackAttachment.setTitleLink(MDC.get("request_context_path"));
        slackAttachment.setFields(getSlackFieldsWithMdc());
        send(e);
        mdcProvider.removeMdc();
    }

    private void send(Exception e){
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackApi.call(slackMessage);
    }

    private List<SlackField> getSlackFieldsWithRequest(HttpServletRequest request){
        return slackKeysEnumSet.stream()
                .map(keys -> keys.addRequest(request))
                .collect(Collectors.toList());
    }

    private List<SlackField> getSlackFieldsWithMdc(){
        slackKeysEnumSet.add(SlackKeys.REQUEST_BODY);
        return slackKeysEnumSet.stream()
                .map(SlackKeys::addMdc)
                .collect(Collectors.toList());
    }
}
