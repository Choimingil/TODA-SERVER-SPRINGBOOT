package com.fineapple.toda.api.abstracts;

import com.fineapple.toda.api.abstracts.delegates.DelegateStatus;
import com.fineapple.toda.api.abstracts.delegates.DelegateDateTime;
import com.fineapple.toda.api.abstracts.delegates.DelegateFile;
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
    protected LocalDateTime toLocalDateTimeFull(String date){
        return delegateDateTime.toLocalDateTimeFull(date);
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
