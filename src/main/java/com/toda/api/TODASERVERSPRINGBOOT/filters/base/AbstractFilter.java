package com.toda.api.TODASERVERSPRINGBOOT.filters.base;

import com.toda.api.TODASERVERSPRINGBOOT.enums.RegularExpressions;
import com.toda.api.TODASERVERSPRINGBOOT.enums.Uris;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractFilter extends OncePerRequestFilter implements BaseFilter {
    private final Set<Uris> uris = EnumSet.allOf(Uris.class);
    private final Set<Uris> validPassUris = EnumSet.of(
            Uris.POST_LOGIN,
            Uris.POST_EMAIL_VALID,
            Uris.GET_TERMS,
            Uris.POST_USER,
            Uris.POST_USER_SEARCHPW
    );


    /**
     * 실제 필터 로직 수행하는 메소드 구현
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try {
            doFilterLogic(request,response);
            filterChain.doFilter(request,response);
        }
        catch(Exception e){
            throwException(request,response,e);
        }
    }

    /**
     * Filter ExceptionHandler
     * @param request
     * @param response
     * @param e
     */
    protected void throwException(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Exception e
    ) {
        getFilterExceptionHandler().getResponse(request, response, e);
    }

    /**
     * 유효한 URI 여부 체크
     * @param request
     * @return
     */
    @Override
    public boolean isValidUri(HttpServletRequest request){
        return uris.contains(Uris.valueOf(getUri(request)));
    }

    /**
     * 토큰이 필요 없는 API 체크
     * @param request
     * @return
     */
    @Override
    public boolean isValidPass(HttpServletRequest request){
        return validPassUris.contains(Uris.valueOf(getUri(request)));
    }

    /**
     * URI를 Enum에 존재하는 값으로 변환
     * @param request
     * @return
     */
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
}
