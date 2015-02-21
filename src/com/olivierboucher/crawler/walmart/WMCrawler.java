package com.olivierboucher.crawler.walmart;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WMCrawler extends EpicerieCrawler {
    public static final int WEBSITE_ID = 2;
    private WebDriver driver;

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
        for(EpicerieStore store : stores) {
            for (EpicerieCategory category : categories) {
                List<EpicerieProduct> list = GetProductsFromCategory(store, category);
                if (list.size() > 0) {
                    return list.get(0);
                }
            }
        }
        return null;
    }
    private List<EpicerieProduct> GetProductsFromCategory(EpicerieStore store, EpicerieCategory category){
        List<EpicerieProduct> list = new ArrayList<EpicerieProduct>();
        // Crawl code
        try {
            int page = 1;
            Boolean doContinue = true;

            while (doContinue) {
                StringBuilder sb = new StringBuilder();
                sb.append("http://www.walmart.ca/fr/epicerie/");
                sb.append(category.getSlug());
                sb.append("+31+12");
                sb.append("/page-");
                sb.append(page);

                Document listDoc = Jsoup.connect(sb.toString()).get();

                if(listDoc.select("article.product").first() != null){
                    Elements elem_items = listDoc.select("article.product");
                    for(Element element : elem_items){
                        if (element.select("div.title > h1 > a").first() != null) {
                            String link = element.select("div.title > h1 > a").first().attr("href");
                            // Navigate to the product link
                            driver.get("http://www.walmart.ca" + link);
                            //Wait up to 5 secs to let the scripts run
                            // THIS CODE HAS TO BE TESTED
                            new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>() {
                                public Boolean apply(WebDriver driver) {
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    return (Boolean) js.executeScript("return jQuery.active == 0");
                                }
                            });
                            Document prodDoc = Jsoup.parse(driver.getPageSource());
                            // Find the body where all infos are
                            if (prodDoc.select("#wrap").first() != null) {
                                Element prodElement = prodDoc.select("#wrap").first();
                                EpicerieProduct product = ExtractProduct(prodElement, parser);
                                if(product != null){
                                    product.setCategory(category);
                                    product.setStore(store);
                                    list.add(product);
                                }
                            }
                        }
                    }
                    page++;
                }
                else{
                    doContinue = false;
                }
            }
            return list;
        }
        catch(Exception e){
            result = Common.CrawlerResult.NetworkError;
            return null;
        }

    }
    private EpicerieProduct ExtractProduct(Element element, EpicerieParser parser){
        parser.setElement(element);
        return parser.getProduct();
    }
    private void Crawl(){
        if(true) {
            for (EpicerieStore store : stores) {
                for (EpicerieCategory category : categories) {
                    List<EpicerieProduct> listFromCategory = GetProductsFromCategory(store, category);
                    if (listFromCategory != null) {
                        products.addAll(listFromCategory);
                    } else {
                        System.out.println(category.getName() + " caused a crash");
                        return;
                    }
                    result = (result == Common.CrawlerResult.Incomplete) ? Common.CrawlerResult.Complete : result;
                }
            }
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
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("phantomjs.page.settings.loadImages", false);
        driver = new PhantomJSDriver(capabilities);
    }
    private boolean NeedsUpdate(){
        return false;
    }
}
