package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.*;

@AllArgsConstructor
@Getter
public final class ValidationException extends RuntimeException{
    int code;
    String message;
}
