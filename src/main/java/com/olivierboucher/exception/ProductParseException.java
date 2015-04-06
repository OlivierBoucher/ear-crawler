package com.olivierboucher.exception;

/**
 * Created by olivierboucher on 15-04-05.
 */
public class ProductParseException extends Exception {
    public ProductParseException(String msg, Exception e) {
        super(msg, e);
    }
}
