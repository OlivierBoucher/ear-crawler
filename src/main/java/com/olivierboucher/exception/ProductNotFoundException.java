package com.olivierboucher.exception;

/**
 * Created by olivier on 2/24/15.
 */
public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String message){
        super(message);
    }
}
