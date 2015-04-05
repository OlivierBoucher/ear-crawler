package com.olivierboucher.exception;

import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieStore;

public class NetworkErrorException extends Exception {
    private EpicerieCrawler crawler;
    private EpicerieStore store;
    private EpicerieCategory category;

    public NetworkErrorException(String message, EpicerieCrawler crawler, EpicerieCategory category, EpicerieStore store, Throwable cause){
        super(message, cause);
        this.crawler = crawler;
        this.category = category;
        this.store = store;
    }
}
