package com.olivierboucher.crawler.walmart;

import com.olivierboucher.crawler.Common;
import com.olivierboucher.crawler.CrawlerJobResult;
import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.ear.MySQLHelper;
import com.olivierboucher.exception.NetworkErrorException;
import com.olivierboucher.exception.ProductNotFoundException;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieStore;
import com.olivierboucher.parser.EpicerieParser;
import com.olivierboucher.parser.walmart.WMEpicerieParser;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

public class WMCrawler extends EpicerieCrawler {
    public static final int WEBSITE_ID = 2;
    // Multithreading usage
    private Stack<EpicerieCategory> categoryStack;

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
    public CrawlerJobResult<EpicerieProduct> StartJobMultiThreaded() throws NetworkErrorException{
        final int THREAD_NUM_MAX = 4;

        try {
            List<Future<List<EpicerieProduct>>> promises = new ArrayList<Future<List<EpicerieProduct>>>();
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM_MAX);
            for(int i=0; i<THREAD_NUM_MAX; i++){
                Callable worker = new WMCallable(i);
                promises.add(executor.submit(worker));
            }
            executor.shutdown();
            while (!executor.isTerminated()){
            }
            for(Future<List<EpicerieProduct>> promise : promises){
                List<EpicerieProduct> gathered = promise.get();
                products.addAll(gathered);
            }
            result = Common.CrawlerResult.Complete;
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        catch(ExecutionException e){
            Class exceptionClass  = e.getCause().getClass();

            if(exceptionClass == NetworkErrorException.class){
                throw (NetworkErrorException)e.getCause();
            }
            else if(exceptionClass == null){
                //Unknown exception
                e.getCause().printStackTrace();
            }
        }

        return new CrawlerJobResult<EpicerieProduct>(products, result);
    }
    public class WMCallable implements Callable<List<EpicerieProduct>> {
        private int id;
        private EpicerieCategory category;
        private EpicerieStore store;
        private WebDriver driver;
        private List<EpicerieProduct> gatheredProducts;
        public WMCallable(int id){
            this.id = id;
            gatheredProducts = new ArrayList<EpicerieProduct>();
            store = stores.get(0);
            DesiredCapabilities capabilities = new DesiredCapabilities();
            String[] phantomArgs = new  String[] {
                    "--webdriver-loglevel=NONE"
            };
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
            capabilities.setCapability("phantomjs.page.settings.loadImages", false);
            this.driver = new PhantomJSDriver(capabilities);
        }
        @Override
        public List<EpicerieProduct> call() throws NetworkErrorException {
            while (true) {
                synchronized (WMCrawler.this){
                    if(!categoryStack.empty()){
                        category=categoryStack.pop();
                        System.out.println("Thread#"+ id + " running category " + category.getName());
                    }
                    else{
                        break;
                    }
                }
                List<EpicerieProduct> productsCategory = GetProductsFromCategory(driver, store, category);
                gatheredProducts.addAll(productsCategory);
            }
            driver.close();
            return gatheredProducts;
        }
    }
    @Override
    public CrawlerJobResult<EpicerieProduct> StartJob() throws NetworkErrorException{
        //TODO : Verify internet connection
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("phantomjs.page.settings.loadImages", false);
        //TODO : Do some tests with a custom proxy, rejecting any scripts/css/images
        //Proxy proxy = new Proxy();
        //proxy.setHttpProxy("");
        //capabilities.setCapability(CapabilityType.PROXY, proxy);

        WebDriver driver = new PhantomJSDriver(capabilities);
        // TODO : Modify for needsUpdate once testing done
        if(true) {
            for (EpicerieStore store : stores) {
                for (EpicerieCategory category : categories) {
                    products.addAll(GetProductsFromCategory(driver, store, category));
                }
            }
        }
        else{
            result = Common.CrawlerResult.UpToDate;
        }
        return new CrawlerJobResult<EpicerieProduct>(products, result);
    }
    public EpicerieProduct GetFirstProductAvailable (WebDriver driver) throws NetworkErrorException, ProductNotFoundException{
        for(EpicerieStore store : stores) {
            for (EpicerieCategory category : categories) {
                List<EpicerieProduct> list = GetProductsFromCategory(driver, store, category);
                if (list.size() > 0) {
                    return list.get(0);
                }
            }
        }
        throw new ProductNotFoundException("Impossible to find any product");
    }
    private List<EpicerieProduct> GetProductsFromCategory(WebDriver driver, EpicerieStore store, EpicerieCategory category) throws NetworkErrorException{
        List<EpicerieProduct> list = new ArrayList<EpicerieProduct>();


            int page = 1;
            for (boolean doContinue = true; doContinue; page++) {
                StringBuilder sb = new StringBuilder();
                sb.append("http://www.walmart.ca/fr/epicerie/");
                sb.append(category.getSlug());
                sb.append("+31+12");
                sb.append("/page-");
                sb.append(page);
                Document listDoc = null;
                try {
                     listDoc = Jsoup.connect(sb.toString()).get();
                }
                catch (HttpStatusException e){
                    doContinue = false;
                    break;
                }
                catch(IOException e){
                    final int MAX_RETRIES = 5;
                    int failCount = 0;

                    while(failCount < MAX_RETRIES){
                        try {
                            listDoc = Jsoup.connect(sb.toString()).get();
                            break;
                        }
                        catch (IOException ioe){
                            failCount++;
                        }

                    }
                    if(failCount == MAX_RETRIES){
                        throw new NetworkErrorException("Cannot connect to website after " + MAX_RETRIES + " tries", this,category,store, e);
                    }
                }

                if(listDoc.select("article.product").first() != null){
                    Elements elem_items = listDoc.select("article.product");
                    for(Element element : elem_items){
                        if (element.select("div.title > h1 > a").first() != null) {
                            String link = element.select("div.title > h1 > a").first().attr("href");
                            try {
                                //Date start = new Date();
                                // Navigate to the product link
                                driver.get("http://www.walmart.ca" + link);
                                //Date end = new Date();
                                //System.out.println("Waited " + Main.getDateDiff(start,end, TimeUnit.MILLISECONDS) + "ms");
                                //Wait up to 5 secs to let the scripts run
                                new WebDriverWait(driver, 8).until(new ExpectedCondition<Boolean>() {
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
                                    if (product != null) {
                                        product.setCategory(category);
                                        product.setStore(store);
                                        list.add(product);
                                    }
                                }
                            }
                            catch(TimeoutException e){
                                System.out.println("Ajax timeout error while loading a page INFO:" + category.toString() + "LINK:" + link);
                            }
                        }
                    }
                }
                else{
                    doContinue = false;
                }
            }
            return list;



    }
    private EpicerieProduct ExtractProduct(Element element, EpicerieParser parser){
        parser.setElement(element);
        return parser.getProduct();
    }
    private void Initialize() throws SQLException {
        parser = new WMEpicerieParser();

        helper.Connect();
        stores = helper.GetStoreList(WEBSITE_ID);
        categories = helper.GetCategoryList(WEBSITE_ID);
        helper.Disconnect();

        //Multithreading
        categoryStack = new Stack<EpicerieCategory>();
        categoryStack.addAll(categories);
    }
    private boolean NeedsUpdate(WebDriver driver){
        return false;
    }
    @Override
    public int getWebsiteId(){
        return WMCrawler.WEBSITE_ID;
    }
}
