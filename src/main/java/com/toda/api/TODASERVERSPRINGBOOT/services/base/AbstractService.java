package com.toda.api.TODASERVERSPRINGBOOT.services.base;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserFcm;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmMap;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.DiaryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractService implements BaseService{
    protected final Logger logger = LoggerFactory.getLogger(AbstractService.class);
    protected final Set<Long> basicStickers = Set.of(1L,2L,3L,4L);

    protected interface MethodParams<T>{ void method(T params); }
    protected interface CheckParams<T> { boolean check(T params); }
    protected interface MethodParams2Params<T,U>{ void method(T param1, U param2); }
    protected interface CheckParams2Params<T, U> { boolean check(T param1, U param2); }

    /**
     * 리스트 중 하나만 로직을 수행하고 나머지는 폐기시켜야 할 때 사용
     * @param check
     * @param params
     * @param entityList
     * @param repository
     * @param <T>
     */
    @Transactional
    protected <T> void updateListAndDelete(CheckParams<T> check, MethodParams<T> params, List<T> entityList, JpaRepository<T, Long> repository) {
        if(!entityList.isEmpty()) {
            for(T entity : entityList){
                if(check.check(entity)){
                    params.method(entity);
                    repository.save(entity);
                }
                else repository.delete(entity);
            }
        }
    }

    /**
     * 리스트 전체를 수정할 떄 사용
     * @param entityList
     * @param params
     * @param repository
     * @param <T>
     */
    @Transactional
    protected <T> void updateList(List<T> entityList, MethodParams<T> params, JpaRepository<T, Long> repository){
        if(!entityList.isEmpty()){
            for(T entity : entityList){
                params.method(entity);
                repository.save(entity);
            }
        }
    }












    /*
    UTILS
     */
    protected long getTimeDiffSec(LocalDateTime currentDateTime, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(targetDateTime, currentDateTime);
        return duration.getSeconds();
    }





    /* FILE */
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
}
