package ru.kdev.kshop.util;

/**
 * @author artem
 */
public interface ThrowableFunction<From, To, Cause extends Throwable> {

    To apply(From from) throws Cause;

}
