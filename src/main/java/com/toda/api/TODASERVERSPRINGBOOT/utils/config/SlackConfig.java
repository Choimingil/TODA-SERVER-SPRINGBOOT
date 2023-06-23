package com.toda.api.TODASERVERSPRINGBOOT.utils.config;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {
    @Value("${slack.webhook-uri}")
    private String slackToken;

    @Bean
    public SlackApi slackApi(){
        return new SlackApi(slackToken);
    }

    @Bean
    public SlackAttachment slackAttachment(){
        return new SlackAttachment();
    }

    @Bean
    public SlackMessage slackMessage(){
        return new SlackMessage();
    }

}