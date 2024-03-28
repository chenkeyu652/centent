package com.centent.core.listener;


import com.centent.core.define.IListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Listener {

    @SuppressWarnings("rawtypes")
    Class<? extends IListener> value();
}
