package com.fineapple.toda.api.abstracts.delegates;

import com.fineapple.toda.api.abstracts.interfaces.BaseUri;
import com.fineapple.toda.api.enums.RegularExpressions;
import com.fineapple.toda.api.enums.Uris;
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
public final class DelegateUri implements BaseUri {
    private final Set<Uris> uris = EnumSet.allOf(Uris.class);
    private final Set<Uris> validPassUris = EnumSet.of(
            Uris.POST_LOGIN,
            Uris.POST_LOGIN_VER2,
            Uris.POST_EMAIL_VALID,
            Uris.GET_TERMS,
            Uris.POST_USER,
            Uris.POST_USER_VER2,
            Uris.POST_USER_SEARCHPW,
            Uris.GET_VALIDATION,
            Uris.GET_UPDATE,
            Uris.GET_UPDATE_VER2
    );

    @Override
    public boolean isValidUri(HttpServletRequest request) {
        List<String> uriList = getUriList(request);
        if(isStaticUri(uriList)) return true;
        return uris.contains(Uris.valueOf(getUriString(uriList)));
    }

    @Override
    public boolean isValidPass(HttpServletRequest request) {
        List<String> uriList = getUriList(request);
        if(isStaticUri(uriList)) return true;
        return validPassUris.contains(Uris.valueOf(getUriString(uriList)));
    }

    /**
     * HttpServletRequest를 받아 List<String<>으로 파싱
     * 메소드,uri( / 단위로 파싱)
     * @param request
     * @return
     */
    private List<String> getUriList(HttpServletRequest request){
        List<String> list = new ArrayList<>(List.of(request.getRequestURI().toUpperCase().trim().split("/")));
        list.set(0, request.getMethod());

        if (list.size() >= 4 && "USERCODE".equals(list.get(1)) && "USER".equals(list.get(3))) {
            list.set(2, "UCVALUE");
        }
        return list;
    }

    /**
     * 정적 파일 읽어오는 uri는 패스
     * @param uriList
     * @return
     */
    private boolean isStaticUri(List<String> uriList){
        return uriList.get(0).equals("GET") && uriList.get(1).equals("UPLOADS");
    }

    /**
     * URI를 Enum에 존재하는 값으로 변환
     * @param uriList
     * @return
     */
    private String getUriString(List<String> uriList) {
        return uriList.stream()
//                .skip(1)
                .map(item -> {
                    if (RegularExpressions.NUMBER.getPattern().matcher(item).matches()) return "NUMBER";
                    else return item;
                })
                .collect(Collectors.joining("_", "", ""));
    }
}
