package com.github.cptblacksheep.launchofexile.exceptions;

import java.io.IOException;

public class ExeNotFoundException extends IOException {

    public ExeNotFoundException() {
    }

    public ExeNotFoundException(String message) {
        super(message);
    }

    public ExeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExeNotFoundException(Throwable cause) {
        super(cause);
    }
}
