package com.centent.core.define;

/**
 * 自定义拦截接口
 *
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public interface Interceptor<P, R> {

    String name();

    default boolean async() {
        return false;
    }

    default boolean supports(P p) {
        return true;
    }

    default boolean beforeSupports(P p) {
        return this.supports(p);
    }

    default boolean afterSupports(P p, R t) {
        return this.supports(p);
    }

    default boolean supports0(Object arg) {
        return this.supports((P) arg);
    }

    default boolean beforeSupports0(Object arg) {
        return this.beforeSupports((P) arg);
    }

    default boolean afterSupports0(Object arg, Object result) {
        return this.afterSupports((P) arg, (R) result);
    }

    default void before0(Object arg) {
        this.before((P) arg);
    }

    default void after0(Object arg, Object result) {
        this.after((P) arg, (R) result);
    }

    default void before(P t) {

    }

    default void after(P p, R t) {

    }
}
