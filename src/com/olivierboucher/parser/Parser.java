package com.olivierboucher.parser;
import org.jsoup.*;
import org.jsoup.nodes.Element;

public abstract class Parser<T> {
    public abstract T getObject();
}
