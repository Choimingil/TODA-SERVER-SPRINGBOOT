package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StickerProvider extends AbstractProvider implements BaseProvider {
    public static Set<Long> BASIC_STICKER_SET = Set.of(1L,2L,3L,4L);
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    // 추후 스티커 관련 로직 수행 예정
}
