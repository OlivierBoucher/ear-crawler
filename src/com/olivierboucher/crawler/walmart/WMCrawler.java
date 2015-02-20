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
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
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
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("phantomjs.page.settings.loadImages", false);
            WebDriver driver = new PhantomJSDriver(capabilities);

            while (doContinue) {
                StringBuilder sb = new StringBuilder();
                sb.append("http://www.walmart.ca/fr/epicerie/");
                sb.append(category.getSlug());
                sb.append("+31+12");
                sb.append("/page-");
                sb.append(page);
                // Pass the url to the phantom driver
                // THIS CODE HAS TO BE TESTED
                WebDriverWait wait = new WebDriverWait(driver,3);
                wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.className("price-current"),"")));
                driver.get(sb.toString());
                // We wait up to 5 secs to let the scripts run
                Thread.sleep(5*1000);

                Document listDoc = Jsoup.parse(driver.getPageSource());

                if(listDoc.select("article.product").first() != null){
                    Elements elem_items = listDoc.select("article.product");
                    for(Element element : elem_items){
                        // Check if price was is there
                        if(element.select("div.price-was").first() != null) {
                            String priceWas = element.select("div.price-was").first().text();
                            if( priceWas != "") {
                                System.out.println("Product has a rebate");
                                // Find the link
                                if (element.select("div.title > h1 > a").first() != null) {
                                    System.out.println("Link found");
                                    String link = element.select("div.title > h1 > a").first().attr("href");
                                    // Navigate to the product link
                                    driver.get("http://www.walmart.ca" + link);
                                    //Wait up to 5 secs to let the scripts run
                                    Thread.sleep(5 * 1000);
                                    Document prodDoc = Jsoup.parse(driver.getPageSource());
                                    // Find the body where all infos are
                                    if (prodDoc.select("#wrap").first() != null) {
                                        System.out.println("Wrap found");
                                        Element prodElement = prodDoc.select("#wrap").first();
                                        EpicerieProduct product = ExtractProduct(prodElement, parser);
                                        if(product != null){
                                            product.setCategory(category);
                                            product.setStore(store);
                                            list.add(product);
                                            System.out.println("Product added " + list.size());
                                        }
                                        else{
                                            System.out.println("Product denied");
                                        }

                                    }
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
    }
    private boolean NeedsUpdate(){
        return false;
    }
}
