package com.olivierboucher.parser;

import com.olivierboucher.exception.ProductParseException;
import com.olivierboucher.model.EpicerieProduct;
import org.jsoup.nodes.Element;

public abstract class EpicerieParser {
    protected Element element;

    public abstract EpicerieProduct getProduct() throws ProductParseException;

    public void setElement(Element element){
        this.element = element;
    }
}
