package com.toda.api.TODASERVERSPRINGBOOT.annotations;

import java.lang.annotation.*;

//@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SetMdcBody {
}
