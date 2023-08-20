package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.Diary;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.DiaryNotice;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserDiary;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.DiaryNoticeRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.DiaryRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserDiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Component("diaryService")
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserDiaryRepository userDiaryRepository;
    private final DiaryNoticeRepository diaryNoticeRepository;
    private final TokenProvider tokenProvider;
    private final Set<DiaryColors> colorSet = EnumSet.allOf(DiaryColors.class);
    private final Set<DiaryStatus> statusSet = EnumSet.allOf(DiaryStatus.class);

    @Transactional
    public long createDiary(String diaryName, int status){
        Diary diary = new Diary();
        diary.setDiaryName(diaryName);
        diary.setStatus(status);
        Diary newDiary = diaryRepository.save(diary);
        return newDiary.getDiaryID();
    }

    @Transactional
    public void setUserDiary(String token, long diaryID, String diaryName, int status){
        long userID = tokenProvider.getUserID(token);
        UserDiary userDiary = new UserDiary();
        userDiary.setUserID(userID);
        userDiary.setDiaryID(diaryID);
        userDiary.setDiaryName(diaryName);
        userDiary.setStatus(status);
        userDiaryRepository.save(userDiary);
    }

    @Transactional
    public void setDiaryNotice(String token, long diaryID, String notice){
        long userID = tokenProvider.getUserID(token);
        DiaryNotice diaryNotice = new DiaryNotice();
        diaryNotice.setUserID(userID);
        diaryNotice.setDiaryID(diaryID);
        diaryNotice.setNotice(notice);
        diaryNoticeRepository.save(diaryNotice);
    }

    public int getDiaryStatus(int status, int color){
        if(status<1 || status>statusSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_STATUS_EXCEPTION);
        if(color<1 || color>colorSet.size()) throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return color*100 + status;
    }

    private String getDiaryColorCode(int color){
        StringBuilder sb = new StringBuilder();
        sb.append("CODE_").append(color);
        String key = sb.toString();
        if(!colorSet.contains(DiaryColors.valueOf(key)))
            throw new WrongArgException(WrongArgException.of.WRONG_DIARY_COLOR_EXCEPTION);
        return DiaryColors.valueOf(key).code;
    }
}
