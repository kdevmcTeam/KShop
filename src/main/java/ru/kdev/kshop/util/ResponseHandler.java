package ru.kdev.kshop.util;

public interface ResponseHandler<H, R> {

    R handleResponse(H handle) throws Exception;

    default void handleException(Throwable throwable) {
        throwable.printStackTrace();
    }
}
