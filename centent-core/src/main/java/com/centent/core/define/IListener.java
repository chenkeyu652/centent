package com.centent.core.define;

/**
 * 自定义拦截接口
 *
 * @since 0.0.1
 */
public interface IListener<P, R> {

    String name();

    default boolean preCheck(P param) {
        return false;
    }

    default boolean postCheck(P param, R result) {
        return false;
    }

    default void preHandle(P param) {

    }

    default void postHandle(P param, R result) {

    }
}
