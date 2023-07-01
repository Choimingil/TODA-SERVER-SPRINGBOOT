package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;
import lombok.*;

@AllArgsConstructor
@Getter
public final class ValidationException extends RuntimeException{
    String exceptionsName;

    public Exceptions getExceptions(){return Exceptions.valueOf(exceptionsName);}

//    public int getCode(){
//        return Exceptions.valueOf(exceptionsName).code();
//    }
//
//    public String getMessage(){
//        return Exceptions.valueOf(exceptionsName).message();
//    }
}
