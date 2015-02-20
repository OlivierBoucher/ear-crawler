package com.olivierboucher.crawler;

import com.olivierboucher.ear.MySQLHelper;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieStore;
import com.olivierboucher.parser.EpicerieParser;
import java.util.List;

public abstract class EpicerieCrawler extends Crawler<EpicerieProduct> {
    protected List<EpicerieProduct> products;
    protected List<EpicerieStore> stores;
    protected List<EpicerieCategory> categories;
    protected EpicerieParser parser;
    protected MySQLHelper helper;
}
