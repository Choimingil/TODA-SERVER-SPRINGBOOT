package com.toda.api.TODASERVERSPRINGBOOT.services.base;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractService implements BaseService{
    protected final Logger logger = LoggerFactory.getLogger(AbstractService.class);
    protected final Set<Long> basicStickers = Set.of(1L,2L,3L,4L);

    /**
     * 파일 이름 받아 파일 읽기
     * @param filename
     * @return
     */
    protected String readTxtFile(String filename) {
        ClassPathResource resource = new ClassPathResource(filename);
        try {
            return readFile(resource).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기로 파일 읽기
     * @param resource
     * @return
     */
    @Async
    private Future<String> readFile(ClassPathResource resource){
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] fileData = FileCopyUtils.copyToByteArray(inputStream);
            return CompletableFuture.completedFuture(new String(fileData, StandardCharsets.UTF_8));
        }
        catch (IOException e){
            throw new WrongAccessException(WrongAccessException.of.READ_TXT_EXCEPTION);
        }
    }

    /**
     * 비동기 메일 전송
     * @param javaMailSender
     * @param message
     * @return
     */
    @Async
    protected Future<Void> sendMail(JavaMailSender javaMailSender, SimpleMailMessage message){
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(null);
    }
}
