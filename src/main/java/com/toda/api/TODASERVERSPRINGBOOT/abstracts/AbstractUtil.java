package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateDateTime;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateFile;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public abstract class AbstractUtil {
    /* Delegate Class */
    private final DelegateDateTime delegateDateTime;
    private final DelegateFile delegateFile;
    private final DelegateStatus delegateStatus;

    protected int getUserDiaryStatus(long userID, long diaryID){
        return delegateStatus.getUserDiaryStatus(userID, diaryID);
    }

    protected int getUserPostStatus(long userID, long postID){
        return delegateStatus.getUserPostStatus(userID,postID);
    }

    protected int getUserCommentStatus(long userID, long commentID){
        return delegateStatus.getUserCommentStatus(userID,commentID);
    }

    protected int getStatus(int firstValue, int secondValue, int digit, Runnable runnable){
        return delegateStatus.getStatus(firstValue,secondValue,digit,runnable);
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
