package com.olivierboucher.crawler;

import com.olivierboucher.crawler.supermarches.SMCrawler;
import com.olivierboucher.crawler.walmart.WMCrawler;

/**
 * Created by olivierboucher on 15-04-04.
 */
public class CrawlerFactory {
    public static EpicerieCrawler GetCrawler(int crawlerId){
        switch (crawlerId){
            case SMCrawler.WEBSITE_ID:
                return new SMCrawler();
            case WMCrawler.WEBSITE_ID:
                return new WMCrawler();
            default:
                return null;
        }
    }
}
