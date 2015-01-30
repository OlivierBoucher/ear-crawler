package com.olivierboucher.parser;
import com.olivierboucher.model.EpicerieElement;
import org.jsoup.*;
import org.jsoup.nodes.Element;

public abstract class AbstractEpicerieParser<T extends EpicerieElement> {
    public abstract T getProducts();
    public abstract T getRebates();
}
