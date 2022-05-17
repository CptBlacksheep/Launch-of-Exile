package com.github.cptblacksheep.launchofexile.exceptions;

public class AhkSupportException extends RuntimeException {

    public AhkSupportException() {

    }

    public AhkSupportException(String message) {
        super(message);
    }

    public AhkSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public AhkSupportException(Throwable cause) {
        super(cause);
    }

    public AhkSupportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
