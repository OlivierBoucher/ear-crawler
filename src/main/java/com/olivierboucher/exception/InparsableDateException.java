package com.olivierboucher.exception;

/**
 * Created by olivierboucher on 15-04-05.
 */
public class InparsableDateException extends Exception {
    public InparsableDateException(String msg, Exception e) {
        super(msg, e);
    }
}
