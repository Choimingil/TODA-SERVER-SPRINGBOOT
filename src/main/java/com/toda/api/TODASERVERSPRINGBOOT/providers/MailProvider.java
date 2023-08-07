package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class MailProvider extends AbstractProvider implements BaseProvider {
    private final JavaMailSender javaMailSender;
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Async
    public Future<Boolean> sendTempPassword(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        StringBuilder sb = new StringBuilder();
        sb.append("임시 비밀번호를 발급했어요! 이 비밀번호로 로그인하시고 마이페이지 -> 비밀번호 변경 에 들어가셔서 비밀번호를 변경해주세요!\n\n").append(password);
        message.setTo(email);
        message.setSubject("TODA에서 편지왔어요 :)");
        message.setText(sb.toString());
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(true);
    }
}
