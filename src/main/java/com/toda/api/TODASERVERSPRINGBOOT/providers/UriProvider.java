package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.RegularExpressions;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Uris;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
public final class UriProvider extends AbstractProvider implements BaseProvider {
    private final EnumSet<Uris> uris = EnumSet.allOf(Uris.class);
    private final EnumSet<Uris> validPassUris = EnumSet.of(
            Uris.POST_LOGIN,
            Uris.POST_EMAIL_VALID
    );

    @Override
    public void afterPropertiesSet() {

    }

    private String getUri(HttpServletRequest request){
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod());
        sb.append("_");
        String[] arr = request.getRequestURI().toUpperCase().trim().split("/");
        for (int i=1;i< arr.length;i++){
            if(RegularExpressions.NUMBER.getPattern().matcher(arr[i]).matches()) sb.append("NUMBER");
            else sb.append(arr[i]);
            if(i<arr.length-1) sb.append("_");
        }
        return sb.toString();
    }

    public boolean isValidUri(HttpServletRequest request){
        return uris.contains(Uris.valueOf(getUri(request)));
    }

    public boolean isValidPass(HttpServletRequest request){
        return validPassUris.contains(Uris.valueOf(getUri(request)));
    }
}
