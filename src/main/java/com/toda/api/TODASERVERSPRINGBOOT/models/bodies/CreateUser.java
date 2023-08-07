package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidEmail;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidPassword;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidUserName;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public final class CreateUser {
    @ValidEmail private String email;
    @ValidPassword @NonNull private String password;
    @ValidUserName @NonNull private String name;
//    private String birth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private String birth = LocalDateTime.now().toString();

    public CreateUser(){}
    @Builder
    public CreateUser(
            String email,
            @NonNull String password,
            @NonNull String name
    ){
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Builder
    public CreateUser(
            String email,
            @NonNull String password,
            @NonNull String name,
            String birth
    ){
        this.email = email;
        this.password = password;
        this.name = name;
        this.birth = birth;
    }
}