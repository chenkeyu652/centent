package com.centent.core.define;

/**
 * 自定义拦截接口
 *
 * @since 0.0.1
 */
public interface Interceptor<P, R> {

    String name();

    @SuppressWarnings("unchecked")
    default void before0(Object arg) {
        this.before((P) arg);
    }

    @SuppressWarnings("unchecked")
    default void after0(Object arg, Object result) {
        this.after((P) arg, (R) result);
    }

    default void before(P t) {

    }

    default void after(P p, R t) {

    }
}
