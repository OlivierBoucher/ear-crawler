package com.olivierboucher.ear;
import com.olivierboucher.crawler.*;

import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.supermarches.SMEpicerieCategory;
import com.olivierboucher.model.supermarches.SMEpicerieStore;
import com.olivierboucher.parser.AbstractEpicerieParser;
import com.olivierboucher.parser.supermarches.SMEpicerieParser;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class EpicerieCrawler extends Crawler<EpicerieProduct> {
	// Global stuff
	private List<EpicerieProduct> products;
	private Common.CrawlerResult globalResult;
	private MySQLHelper helper;
	// Supermarche related fields
	private List<SMEpicerieStore> SMStores;
	private List<SMEpicerieCategory> SMCategories;
	private Common.CrawlerResult SMResult;
	private SMEpicerieParser SMParser;

	public EpicerieCrawler(){
		helper = new MySQLHelper();
		products = new ArrayList<EpicerieProduct>();
		//Initialize different websites here
		try{
			InitializeSM();
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
		CrawlSM();
		// Build result map
		HashMap<String, Common.CrawlerResult> resultMap = new HashMap();
		resultMap.put("SM", SMResult);

		return new CrawlerJobResult<EpicerieProduct>(products, resultMap);
	}

	public EpicerieProduct SMGetFirstProductAvailable(){
		for(SMEpicerieStore store : SMStores){
			for(SMEpicerieCategory category : SMCategories){
				List<EpicerieProduct> list = SMGetProductsFromCategory(store, category);
				if(list.size() > 0){
					return list.get(0);
				}
			}
		}
		return null;
	}
	private List<EpicerieProduct> SMGetProductsFromCategory(SMEpicerieStore store, SMEpicerieCategory category){
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
						EpicerieProduct product = ExtractProduct(element, SMParser);
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
			SMResult = Common.CrawlerResult.NetworkError;
			return null;
		}
	}
	private EpicerieProduct ExtractProduct(Element element, AbstractEpicerieParser parser){
		parser.setElement(element);
		return parser.getProduct();
	}
	private void CrawlSM(){
		if(SMNeedsUpdate()) {
			for (SMEpicerieStore store : SMStores) {
				for (SMEpicerieCategory category : SMCategories) {
					products.addAll(SMGetProductsFromCategory(store, category));
				}
			}
			SMResult = (SMResult == Common.CrawlerResult.Incomplete) ? Common.CrawlerResult.Complete : SMResult;
		}
		else{
			SMResult = Common.CrawlerResult.UpToDate;
		}
	}
	private void InitializeSM() throws SQLException{
		SMResult = Common.CrawlerResult.Incomplete;
		SMParser = new SMEpicerieParser();
		helper.Connect();
		SMStores = helper.SMGetStoreList();
		SMCategories = helper.SMGetCategoryList();
		helper.Disconnect();
	}
	private  boolean SMNeedsUpdate(){
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
