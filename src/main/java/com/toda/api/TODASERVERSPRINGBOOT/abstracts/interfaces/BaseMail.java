package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import org.springframework.mail.SimpleMailMessage;

import java.util.concurrent.Future;

public interface BaseMail {
    /**
     * 비동기 메일 전송
     * @param message
     * @return
     */
    Future<Void> sendMail(SimpleMailMessage message);
}
