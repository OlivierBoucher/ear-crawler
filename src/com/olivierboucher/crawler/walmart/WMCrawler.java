package com.olivierboucher.crawler.walmart;

import com.olivierboucher.crawler.Common;
import com.olivierboucher.crawler.Crawler;
import com.olivierboucher.crawler.CrawlerJobResult;
import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.ear.MySQLHelper;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieStore;
import com.olivierboucher.parser.EpicerieParser;
import com.olivierboucher.parser.walmart.WMEpicerieParser;
import org.jsoup.nodes.Element;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WMCrawler extends EpicerieCrawler {
    public static final int WEBSITE_ID = 2;

    public WMCrawler(){
        helper = new MySQLHelper();
        products = new ArrayList<EpicerieProduct>();
        try{
            Initialize();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public CrawlerJobResult<EpicerieProduct> StartJobMultiThreaded() {
        return null;
    }
    @Override
    public CrawlerJobResult<EpicerieProduct> StartJob() {
        // TODO : Verify internet connection
        Crawl();
        return new CrawlerJobResult<EpicerieProduct>(products, result);
    }

    public EpicerieProduct GetFirstProductAvailable(){
        for(EpicerieStore store : stores){
            for(EpicerieCategory category : categories){
                List<EpicerieProduct> list = GetProductsFromCategory(store, category);
                if(list.size() > 0){
                    return list.get(0);
                }
            }
        }
        return null;
    }
    private List<EpicerieProduct> GetProductsFromCategory(EpicerieStore store, EpicerieCategory category){
        return null;
    }
    private EpicerieProduct ExtractProduct(Element element, EpicerieParser parser){
        parser.setElement(element);
        return parser.getProduct();
    }
    private void Crawl(){
        if(NeedsUpdate()) {
            for (EpicerieStore store : stores) {
                for (EpicerieCategory category : categories) {
                    products.addAll(GetProductsFromCategory(store, category));
                }
            }
            result = (result == Common.CrawlerResult.Incomplete) ? Common.CrawlerResult.Complete : result;
        }
        else{
            result = Common.CrawlerResult.UpToDate;
        }
    }
    private void Initialize() throws SQLException {
        result = Common.CrawlerResult.Incomplete;
        parser = new WMEpicerieParser();
        helper.Connect();
        stores = helper.GetStoreList(WEBSITE_ID);
        categories = helper.GetCategoryList(WEBSITE_ID);
        helper.Disconnect();
    }
    private boolean NeedsUpdate(){
        return false;
    }
}
