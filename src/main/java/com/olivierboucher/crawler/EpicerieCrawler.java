package com.olivierboucher.crawler;

import com.olivierboucher.ear.MySQLHelper;
import com.olivierboucher.exception.UnrecoverableException;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieStore;
import com.olivierboucher.parser.EpicerieParser;

import java.util.List;

public abstract class EpicerieCrawler {
    protected Common.CrawlerResult result;
    protected List<EpicerieProduct> products;
    protected List<EpicerieStore> stores;
    protected List<EpicerieCategory> categories;
    protected EpicerieParser parser;
    protected MySQLHelper helper;
    private boolean execMultiThreaded;

    public abstract EpicerieCrawlerJobResult StartJobMultiThreaded() throws Exception;

    public abstract EpicerieCrawlerJobResult StartJob() throws Exception;

    public EpicerieCrawlerJobResult StartPreferedJob() throws UnrecoverableException {
        try {
            if (isExecMultiThreaded()) {
                return StartJobMultiThreaded();
            } else {
                return StartJob();
            }
        }catch (Exception e){
            System.out.print(e.getStackTrace());
            return null;
        }
    }

    public boolean isExecMultiThreaded() {
        return execMultiThreaded;
    }

    public void setExecMultiThreaded(boolean execMultiThreaded) {
        this.execMultiThreaded = execMultiThreaded;
    }
    public abstract int getWebsiteId();
}
