package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BaseService {
//    interface MethodNoParams{ void method(); }
//    interface MethodParams<T>{ void method(T params); }
//    interface CheckParams<T> { boolean check(T params); }
//    interface MethodParams2Params<T,U>{ void method(T param1, U param2); }
//    interface CheckParams2Params<T, U> { boolean check(T param1, U param2); }
//    interface FcmMethod<T, U>{ FcmGroup method(T param1, U param2); }

    /**
     * 리스트 중 하나만 로직을 수행하고 나머지는 폐기시켜야 할 때 사용
     * @param check
     * @param run
     * @param entityList
     * @param repository
     * @param <T>
     */
    <T> void updateListAndDelete(Function<T,Boolean> check, Consumer<T> run, List<T> entityList, JpaRepository<T, Long> repository);

    /**
     * 리스트 전체를 수정할 떄 사용
     * @param entityList
     * @param run
     * @param repository
     * @param <T>
     */
    <T> void updateList(List<T> entityList, Consumer<T> run, JpaRepository<T, Long> repository);
}
