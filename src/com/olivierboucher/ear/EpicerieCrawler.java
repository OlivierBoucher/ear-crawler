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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EpicerieCrawler extends Crawler<EpicerieProduct> {
	// Global stuff
	private List<EpicerieProduct> products;
	private Common.CrawlerResult globalResult;
	// Supermarche related fields
	private List<SMEpicerieStore> SMStores;
	private List<SMEpicerieCategory> SMCategories;
	private Common.CrawlerResult SMResult;
	private SMEpicerieParser SMParser;
	
	public EpicerieCrawler(){
		products = new ArrayList<EpicerieProduct>();
		//Initialize different websites here
		InitializeSM();
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
				SMGetProductsFromCategory(store, category);
				if(products.size() > 0){
					return products.get(0);
				}
			}
		}
		return null;
	}
	private void SMGetProductsFromCategory(SMEpicerieStore store, SMEpicerieCategory category){
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
						products.add(product);
					}
					page++;
				}
				else{
					doContinue = false;
				}
			}
			
		} 
		catch (IOException ioe) {
			SMResult = Common.CrawlerResult.NetworkError;
		}

		
	}
	private EpicerieProduct ExtractProduct(Element element, AbstractEpicerieParser parser){
		parser.setElement(element);
		return parser.getProduct();
	}
	private void CrawlSM(){
		for(SMEpicerieStore store : SMStores){
			for(SMEpicerieCategory category : SMCategories){
				SMGetProductsFromCategory(store, category);
			}
		}
		SMResult = (SMResult == Common.CrawlerResult.Incomplete) ? Common.CrawlerResult.Complete : SMResult;
	}
	private void InitializeSM(){
		SMResult = Common.CrawlerResult.Incomplete;
		SMParser = new SMEpicerieParser();
		//TODO : Load theses from database
		SMStores = new ArrayList<SMEpicerieStore>();
		SMStores.add(new SMEpicerieStore("IGA"));
		SMStores.add(new SMEpicerieStore("LOBLAWS"));
		SMStores.add(new SMEpicerieStore("METRO"));
		SMStores.add(new SMEpicerieStore("MAXI"));
		SMStores.add(new SMEpicerieStore("SUPER C"));
		// TODO : Same
		SMCategories = new ArrayList<SMEpicerieCategory>();
		SMCategories.add (new SMEpicerieCategory(612, "Aliments surgelés"));
		SMCategories.add (new SMEpicerieCategory(3, "Bières et vins"));
		SMCategories.add (new SMEpicerieCategory(4, "Biscuits, collations et friandises"));
		SMCategories.add (new SMEpicerieCategory(5, "Boissons, eau et jus"));
		SMCategories.add (new SMEpicerieCategory(6, "Boulangerie et pâtisserie"));
		SMCategories.add (new SMEpicerieCategory(8, "Café, thé et tisanes"));
		SMCategories.add (new SMEpicerieCategory(16, "Céréales, gruau et riz"));
		SMCategories.add (new SMEpicerieCategory(9, "Charcuterie"));
		SMCategories.add (new SMEpicerieCategory(340, "Condiments, huiles et sauces"));
		SMCategories.add (new SMEpicerieCategory(10,"Confitures, tartinades et sirops"));
		SMCategories.add (new SMEpicerieCategory(452, "Conserves, prêt-à-manger et soupes"));
		SMCategories.add (new SMEpicerieCategory(11, "Fruits de mer et poissons"));
		SMCategories.add (new SMEpicerieCategory(12, "Fruits, légumes et noix"));
		SMCategories.add (new SMEpicerieCategory(13, "Pâtes, farine et oeufs"));
		SMCategories.add (new SMEpicerieCategory(15, "Produits laitiers"));
		SMCategories.add (new SMEpicerieCategory(17,"Viandes"));
		SMCategories.add (new SMEpicerieCategory(18, "Volaille"));
		SMCategories.add (new SMEpicerieCategory(23,"Contenants, outils et papiers"));
		SMCategories.add (new SMEpicerieCategory(22, "Produits ménagers"));
		SMCategories.add (new SMEpicerieCategory(20, "Produits pour les animaux"));
		SMCategories.add (new SMEpicerieCategory(648,"Produits pour les bébés"));
		SMCategories.add (new SMEpicerieCategory(21, "Soins du corps"));
		SMCategories.add (new SMEpicerieCategory(520, "Divers"));
	}
}
