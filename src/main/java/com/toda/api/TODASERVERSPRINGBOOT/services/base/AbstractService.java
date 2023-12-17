package com.toda.api.TODASERVERSPRINGBOOT.services.base;

import com.toda.api.TODASERVERSPRINGBOOT.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserLog;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserFcm;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmMap;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.KafkaFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserFcmProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.DiaryProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserDiaryRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractService implements BaseService{
    protected final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    protected final Set<Long> basicStickers = Set.of(1L,2L,3L,4L);
    protected final Set<DiaryColors> colorSet = EnumSet.allOf(DiaryColors.class);
    protected final Set<DiaryStatus> statusSet = EnumSet.allOf(DiaryStatus.class);


    protected interface MethodNoParams{ void method(); }
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
            List<T> saveList = new ArrayList<>();
            List<T> deleteList = new ArrayList<>();
            for(T entity : entityList){
                if(check.check(entity)){
                    params.method(entity);
                    saveList.add(entity);
                }
                else deleteList.add(entity);
            }
            if(!saveList.isEmpty()) repository.saveAll(saveList);
            if(!deleteList.isEmpty()) repository.deleteAll(deleteList);
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
            List<T> res = new ArrayList<>();
            for(T entity : entityList){
                params.method(entity);
                res.add(entity);
            }
            repository.saveAll(res);
        }
    }












    /*
    GETTER
     */

    /**
     * 유저가 다이어리에 어떤 상태로 존재하는지 확인
     * @param userID
     * @param diaryID
     * @return 404,100,200
     * 404 : 유저가 다이어리에 속하지 않을 경우
     * 100 : 유저가 다이어리에 속할 경우
     * 200 : 유저가 다이어리에 속하지 않고 초대 요청이 온 경우
     */
    protected int getUserDiaryStatus(long userID, long diaryID, UserDiaryRepository userDiaryRepository){
        List<UserDiary> userDiaryList = userDiaryRepository.findByUserIDAndDiaryIDAndStatusNot(userID,diaryID,999);
        if(userDiaryList.isEmpty()) return 404;
        for(UserDiary userDiary : userDiaryList) if(userDiary.getStatus()%10 != 0) return 100;
        return 200;
    }

    /**
     * 데이터의 상태값을 생성
     * @param firstValue
     * @param secondValue
     * @param params
     * @return firstValue*100 + secondValue 형식
     */
    protected int getStatus(int firstValue, int secondValue, MethodNoParams params){
        params.method();
        return firstValue*100 + secondValue;
    }

    /**
     * "yyyy-MM-dd" 형식의 날짜 String을 LocalDateTime으로 변환
     * @param date
     * @return
     */
    protected LocalDateTime toLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localdate = LocalDate.parse(date, formatter);
        return LocalDateTime.of(localdate, LocalDateTime.now().toLocalTime());
    }

    /**
     * 두 시간 사이의 초 시간 리턴
     * @param currentDateTime
     * @param targetDateTime
     * @return
     */
    protected long getTimeDiffSec(LocalDateTime currentDateTime, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(targetDateTime, currentDateTime);
        return duration.getSeconds();
    }

    protected long getUserID(String token, TokenProvider tokenProvider){
        return tokenProvider.getUserID(token);
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
