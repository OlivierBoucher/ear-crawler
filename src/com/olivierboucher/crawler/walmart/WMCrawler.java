package com.olivierboucher.crawler.walmart;

import com.olivierboucher.crawler.Crawler;
import com.olivierboucher.crawler.CrawlerJobResult;
import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.model.EpicerieProduct;

public class WMCrawler extends EpicerieCrawler {
    @Override
    public CrawlerJobResult<EpicerieProduct> StartJobMultiThreaded() {
        return null;
    }
    @Override
    public CrawlerJobResult<EpicerieProduct> StartJob() {
        return null;
    }
}
