package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.enums.RegularExpressions;
import com.toda.api.TODASERVERSPRINGBOOT.enums.Uris;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class UriProvider extends AbstractProvider implements BaseProvider {
    private final Set<Uris> uris = EnumSet.allOf(Uris.class);
    private final Set<Uris> validPassUris = EnumSet.of(
            Uris.POST_LOGIN,
            Uris.POST_EMAIL_VALID,
            Uris.GET_TERMS,
            Uris.POST_USER,
            Uris.POST_USER_SEARCHPW
    );

    @Override
    public void afterPropertiesSet() {

    }

    private String getUri(HttpServletRequest request){
        /**
         * uri = /url_name 이기 때문에 /으로 파싱하면 맨 앞이 공백, 따라서 맨 앞을 스킵
         */
        List<String> list = new ArrayList<>(List.of(request.getRequestURI().toUpperCase().trim().split("/")));
        list.add(1,request.getMethod());
        return list.stream()
                .skip(1)
                .map(item -> {
                    if (RegularExpressions.NUMBER.getPattern().matcher(item).matches()) return "NUMBER";
                    else return item;
                })
                .collect(Collectors.joining("_", "", ""));
    }

    public boolean isValidUri(HttpServletRequest request){
        return uris.contains(Uris.valueOf(getUri(request)));
    }

    public boolean isValidPass(HttpServletRequest request){
        return validPassUris.contains(Uris.valueOf(getUri(request)));
    }
}
