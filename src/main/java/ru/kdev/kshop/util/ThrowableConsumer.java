package ru.kdev.kshop.util;

public interface ThrowableConsumer<Object, Cause extends Throwable> {

    void accept(Object object) throws Cause;

}
