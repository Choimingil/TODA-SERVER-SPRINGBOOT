package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.fcms.FcmGroup;

public interface MethodParamsInterface {
    interface MethodNoParams{ void method(); }
    interface MethodParams<T>{ void method(T params); }
    interface CheckParams<T> { boolean check(T params); }
    interface MethodParams2Params<T,U>{ void method(T param1, U param2); }
    interface CheckParams2Params<T, U> { boolean check(T param1, U param2); }
    interface FcmMethod<T, U>{ FcmGroup method(T param1, U param2); }
}
