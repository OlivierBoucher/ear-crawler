package com.olivierboucher.exception;

/**
 * Created by olivierboucher on 15-04-05.
 */
public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String msg, Exception e) {
        super(msg, e);
    }

    public ElementNotFoundException(String msg) {
        super(msg);
    }
}
