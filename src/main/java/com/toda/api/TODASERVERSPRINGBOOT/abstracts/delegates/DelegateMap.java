package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseMap;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public final class DelegateMap implements BaseMap {
    @Override
    public <T,U> Map<T,U> getMap(List<T> keyList, List<U> valueList) {
        if(keyList.size() != valueList.size()) throw new WrongArgException(WrongArgException.of.KEY_VALUE_SIZE_EXCEPTION);
        return IntStream.range(0, keyList.size()).boxed().collect(Collectors.toMap(keyList::get, valueList::get));
    }
}
