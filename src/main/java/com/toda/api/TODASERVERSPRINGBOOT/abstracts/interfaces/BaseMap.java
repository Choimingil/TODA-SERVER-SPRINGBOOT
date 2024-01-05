package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import java.util.List;
import java.util.Map;

public interface BaseMap {
    /**
     * keyList와 valueList를 받아서 Map을 생성
     * Value : Object Type
     * @param keyList
     * @param valueList
     * @return
     */
    <T,U> Map<T,U> getMap(List<T> keyList, List<U> valueList);
}
