package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateFile;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateStatus;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.MethodParamsInterface;
import com.toda.api.TODASERVERSPRINGBOOT.entities.*;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryColors;
import com.toda.api.TODASERVERSPRINGBOOT.enums.DiaryStatus;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.CommentRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.PostRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserDiaryRepository;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public abstract class AbstractService extends AbstractFcm implements BaseService, MethodParamsInterface {
    protected final Logger logger = LoggerFactory.getLogger(AbstractService.class);
    protected final Set<Long> basicStickers = Set.of(1L,2L,3L,4L);
    protected final Set<DiaryColors> colorSet = EnumSet.allOf(DiaryColors.class);
    protected final Set<DiaryStatus> statusSet = EnumSet.allOf(DiaryStatus.class);

    /* Delegate Class */
    private final DelegateDateTime delegateDateTime = new DelegateDateTime();
    private final DelegateFile delegateFile = new DelegateFile();
    private final DelegateStatus delegateStatus = new DelegateStatus();

//    private final DelegateDateTime delegateDateTime;
//    private final DelegateFile delegateFile;
//    private final DelegateStatus delegateStatus;

//    public AbstractService(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus) {
//        this.delegateDateTime = delegateDateTime;
//        this.delegateFile = delegateFile;
//        this.delegateStatus = delegateStatus;
//    }


    @Override
    @Transactional
    public <T> void updateListAndDelete(CheckParams<T> check, MethodParams<T> params, List<T> entityList, JpaRepository<T, Long> repository) {
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

    @Override
    @Transactional
    public <T> void updateList(List<T> entityList, MethodParams<T> params, JpaRepository<T, Long> repository){
        if(!entityList.isEmpty()){
            List<T> res = new ArrayList<>();
            for(T entity : entityList){
                params.method(entity);
                res.add(entity);
            }
            repository.saveAll(res);
        }
    }

    protected int getUserDiaryStatus(long userID, long diaryID, UserDiaryRepository userDiaryRepository){
        return delegateStatus.getUserDiaryStatus(userID, diaryID, userDiaryRepository);
    }

    protected int getUserPostStatus(long userID, long postID, UserDiaryRepository userDiaryRepository, PostRepository postRepository){
        return delegateStatus.getUserPostStatus(userID,postID,userDiaryRepository,postRepository);
    }

    protected int getUserCommentStatus(long userID, long commentID, CommentRepository commentRepository){
        return delegateStatus.getUserCommentStatus(userID,commentID,commentRepository);
    }

    protected int getStatus(int firstValue, int secondValue, int digit, MethodNoParams params){
        return delegateStatus.getStatus(firstValue,secondValue,digit,params);
    }

    protected String toStringDateFullTime(LocalDateTime dateTime){
        return delegateDateTime.toStringDateFullTime(dateTime);
    }

    protected LocalDateTime toLocalDateTime(String date){
        return delegateDateTime.toLocalDateTime(date);
    }

    protected long getTimeDiffSec(LocalDateTime currentDateTime, LocalDateTime targetDateTime) {
        return delegateDateTime.getTimeDiffSec(currentDateTime,targetDateTime);
    }

    protected String getDateString(LocalDateTime targetDateTime){
        return delegateDateTime.getDateString(targetDateTime);
    }

    protected String readTxtFile(String filename) {
        return delegateFile.readTxtFile(filename);
    }
}
