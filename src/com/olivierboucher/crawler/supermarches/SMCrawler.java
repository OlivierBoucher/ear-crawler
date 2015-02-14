package com.olivierboucher.crawler.supermarches;
import com.olivierboucher.crawler.*;

import com.olivierboucher.ear.MySQLHelper;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.supermarches.SMEpicerieCategory;
import com.olivierboucher.model.supermarches.SMEpicerieStore;
import com.olivierboucher.parser.EpicerieParser;
import com.olivierboucher.parser.supermarches.SMEpicerieParser;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SMCrawler extends EpicerieCrawler {

	private MySQLHelper helper;
	private List<SMEpicerieStore> stores;
	private List<SMEpicerieCategory> categories;

	public SMCrawler(){
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

	public EpicerieProduct SMGetFirstProductAvailable(){
		for(SMEpicerieStore store : stores){
			for(SMEpicerieCategory category : categories){
				List<EpicerieProduct> list = GetProductsFromCategory(store, category);
				if(list.size() > 0){
					return list.get(0);
				}
			}
		}
		return null;
	}
	private List<EpicerieProduct> GetProductsFromCategory(SMEpicerieStore store, SMEpicerieCategory category){
		List<EpicerieProduct> list = new ArrayList<EpicerieProduct>();
		// Crawl code
		try {
			int page = 1;
			Boolean doContinue = true;
			while(doContinue){
				StringBuilder sb = new StringBuilder();
				sb.append("http://www.supermarches.ca/pages/Aubaines.asp?vd=");
				sb.append(store.getName());
				sb.append("&cid=");
				sb.append(category.getId());
				sb.append("&page=");
				sb.append(page);

				Document doc = Jsoup.connect(sb.toString()).get();

				if(doc.select("tbody tr [onmouseover=this.bgColor = '#FFFFD9']").first() != null){
					Elements elem_items = doc.select("tbody tr [onmouseover=this.bgColor = '#FFFFD9']");
					for(Element element : elem_items){
						EpicerieProduct product = ExtractProduct(element, parser);
						product.setCategory(category);
						product.setStore(store);
						list.add(product);
					}
					page++;
				}
				else{
					doContinue = false;
				}
			}
			return list;
		}
		catch (IOException ioe) {
			result = Common.CrawlerResult.NetworkError;
			return null;
		}
	}
	private EpicerieProduct ExtractProduct(Element element, EpicerieParser parser){
		parser.setElement(element);
		return parser.getProduct();
	}
	private void Crawl(){
		if(NeedsUpdate()) {
			for (SMEpicerieStore store : stores) {
				for (SMEpicerieCategory category : categories) {
					products.addAll(GetProductsFromCategory(store, category));
				}
			}
			result = (result == Common.CrawlerResult.Incomplete) ? Common.CrawlerResult.Complete : result;
		}
		else{
			result = Common.CrawlerResult.UpToDate;
		}
	}
	private void Initialize() throws SQLException{
		result = Common.CrawlerResult.Incomplete;
		parser = new SMEpicerieParser();
		helper.Connect();
		stores = helper.SMGetStoreList();
		categories = helper.SMGetCategoryList();
		helper.Disconnect();
	}
	private boolean NeedsUpdate(){
		Date online_date = SMGetFirstProductAvailable().getRebate().getStart();
		Date db_date = null;
		// Get date from database
		helper.Connect();
		try {
			db_date = helper.SMGetActualStartDate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		helper.Disconnect();
		// Comparaison
		if(db_date == null || !(db_date.equals(online_date))){
			return true;
		}
		else{
			return false;
		}
	}
}
