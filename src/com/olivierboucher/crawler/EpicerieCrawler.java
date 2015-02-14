package com.olivierboucher.crawler;

import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.parser.EpicerieParser;

import java.util.List;

/**
 * Created by olivierboucher on 15-02-14.
 */
public abstract class EpicerieCrawler extends Crawler<EpicerieProduct> {
    protected List<EpicerieProduct> products;
    protected EpicerieParser parser;
}
