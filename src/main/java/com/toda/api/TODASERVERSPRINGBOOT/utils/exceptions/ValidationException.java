package com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions;

import lombok.*;

@AllArgsConstructor
@Getter
public class ValidationException extends RuntimeException{
    int code;
    String message;
}
