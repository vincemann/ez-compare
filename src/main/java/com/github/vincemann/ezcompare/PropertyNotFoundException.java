package com.github.vincemann.ezcompare;

public class PropertyNotFoundException  extends RuntimeException{

    PropertyNotFoundException() {
    }

    PropertyNotFoundException(String message) {
        super(message);
    }

    PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    PropertyNotFoundException(Throwable cause) {
        super(cause);
    }

    PropertyNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
