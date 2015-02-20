package com.olivierboucher.parser;

import com.olivierboucher.model.EpicerieProduct;
import org.jsoup.nodes.Element;

public abstract class EpicerieParser {
    protected Element element;
    public abstract EpicerieProduct getProduct();

    public void setElement(Element element){
        this.element = element;
    }
}
