package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaseService {
    /**
     * 리스트 중 하나만 로직을 수행하고 나머지는 폐기시켜야 할 때 사용
     * @param check
     * @param params
     * @param entityList
     * @param repository
     * @param <T>
     */
    <T> void updateListAndDelete(MethodParamsInterface.CheckParams<T> check, MethodParamsInterface.MethodParams<T> params, List<T> entityList, JpaRepository<T, Long> repository);

    /**
     * 리스트 전체를 수정할 떄 사용
     * @param entityList
     * @param params
     * @param repository
     * @param <T>
     */
    <T> void updateList(List<T> entityList, MethodParamsInterface.MethodParams<T> params, JpaRepository<T, Long> repository);
}
