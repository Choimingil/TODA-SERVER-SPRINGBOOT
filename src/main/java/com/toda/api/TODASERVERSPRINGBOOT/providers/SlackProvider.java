package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.enums.SlackKeys;
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
    private final Set<SlackKeys> slackKeysEnumSet = EnumSet.allOf(SlackKeys.class);

    @Override
    public void afterPropertiesSet() {
        slackKeysEnumSet.remove(SlackKeys.REQUEST_BODY);
    }

    public void doSlack(HttpServletRequest request, Exception e) {
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setFields(getSlackFields(request));
        send(e);
    }

    @Async
    public void doSlack(Exception e) {
        slackAttachment.setTitleLink(MDC.get("request_context_path"));
        slackAttachment.setFields(getSlackFields());
        send(e);
        mdcProvider.removeMdc();
    }

    private void send(Exception e){
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackApi.call(slackMessage);
    }

    private List<SlackField> getSlackFields(HttpServletRequest request){
        return slackKeysEnumSet.stream()
                .map(keys -> keys.addRequest(request))
                .collect(Collectors.toList());
    }

    private List<SlackField> getSlackFields(){
        slackKeysEnumSet.add(SlackKeys.REQUEST_BODY);
        return slackKeysEnumSet.stream()
                .map(SlackKeys::addMdc)
                .collect(Collectors.toList());
    }
}
