package com.katasoft.beanpole.util;

/**
 * Generics-aware interface supporting the
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">Factory Method</a> design pattern.
 *
 * @param <T> The type of the instance returned by the Factory implementation.
 * @since 0.1
 */
public interface Factory<T> {

    /**
     * Returns an instance of the required type.  The implementation determines whether or not a new or cached
     * instance is created every time this method is called.
     *
     * @return an instance of the required type.
     */
    T getInstance();
}
