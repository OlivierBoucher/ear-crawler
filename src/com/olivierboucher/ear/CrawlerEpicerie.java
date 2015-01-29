package com.olivierboucher.ear;
import com.olivierboucher.crawler.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CrawlerEpicerie extends Crawler {
	
	private List<Vendor> vendors;
	private List<Category> categories;
	private List<Product> products;
	private Common.CrawlerResult result;
	
	public CrawlerEpicerie(){
		products = new ArrayList<Product>();
		result = Common.CrawlerResult.Incomplete;
		
		vendors = new ArrayList<Vendor>();
		vendors.add(new Vendor("IGA"));
		vendors.add(new Vendor("LOBLAWS"));
		vendors.add(new Vendor("METRO"));
		vendors.add(new Vendor("MAXI"));
		vendors.add(new Vendor("SUPER C"));
		
		categories = new ArrayList<Category>();
		categories.add (new Category(612, "Aliments surgelés"));
		categories.add (new Category(3, "Bières et vins"));
		categories.add (new Category(4, "Biscuits, collations et friandises"));
		categories.add (new Category(5, "Boissons, eau et jus"));
		categories.add (new Category(6, "Boulangerie et pâtisserie"));
		categories.add (new Category(8, "Café, thé et tisanes"));
		categories.add (new Category(16, "Céréales, gruau et riz"));
		categories.add (new Category(9, "Charcuterie"));
		categories.add (new Category(340, "Condiments, huiles et sauces"));
		categories.add (new Category(10,"Confitures, tartinades et sirops"));
		categories.add (new Category(452, "Conserves, prêt-à-manger et soupes"));
		categories.add (new Category(11, "Fruits de mer et poissons"));
		categories.add (new Category(12, "Fruits, légumes et noix"));
		categories.add (new Category(13, "Pâtes, farine et oeufs"));
		categories.add (new Category(15, "Produits laitiers"));
		categories.add (new Category(17,"Viandes"));
		categories.add (new Category(18, "Volaille"));
		categories.add (new Category(23,"Contenants, outils et papiers"));
		categories.add (new Category(22, "Produits ménagers"));
		categories.add (new Category(20, "Produits pour les animaux"));
		categories.add (new Category(648,"Produits pour les bébés"));
		categories.add (new Category(21, "Soins du corps"));
		categories.add (new Category(520, "Divers"));
	}
	public CrawlerEpicerie(String[] vendors){
		
	}

	@Override
	public CrawlerJobResult<Product> StartJobMultiThreaded() {
		return null;
	}
	@Override
	public CrawlerJobResult<Product> StartJob() {
		// TODO : Verify internet connection
		for(Vendor vendor : vendors){
			for(Category category : categories){
				GetProductsFromCategory(vendor, category);
			}
		}
		result = (result == Common.CrawlerResult.Incomplete) ? Common.CrawlerResult.Complete : result;
				
		return new CrawlerJobResult<Product>(products,result);
	}
	public Product GetFirstProductAvailable(){
		for(Vendor vendor : vendors){
			for(Category category : categories){
				GetProductsFromCategory(vendor,category);
				if(products.size() > 0){
					return products.get(0);
				}
			}
		}
		return null;
	}
	private void GetProductsFromCategory(Vendor vendor, Category category){
		// Crawl code
		try {
			int page = 1;
			Boolean doContinue = true;
			while(doContinue){
				StringBuilder sb = new StringBuilder();
				sb.append("http://www.supermarches.ca/pages/Aubaines.asp?vd=");
				sb.append(vendor.getName());
				sb.append("&cid=");
				sb.append(category.getId());
				sb.append("&page=");
				sb.append(page);

				Document doc = Jsoup.connect(sb.toString()).get();
				
				if(doc.select("tbody tr [onmouseover=this.bgColor = '#FFFFD9']").first() != null){
					Elements elem_items = doc.select("tbody tr [onmouseover=this.bgColor = '#FFFFD9']");
					for(Element element : elem_items){
						Product product = ExtractProduct(element);
						product.setCategory(category);
						product.setVendor(vendor);
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
			result = Common.CrawlerResult.NetworkError;
		}

		
	}
	private Product ExtractProduct(Element element){
		/*
		 * Regexes 
		 * 
		 * 16.99 / ch.             -> / \d+.\d{2}[[:space:]]?\/[[:space:]]?ch. /
		 * 16.99 / sac             -> / \d+.\d{2}[[:space:]]?\/[[:space:]]?sac /
		 * 16.99 / pqt             -> / \d+.\d{2}[[:space:]]?\/[[:space:]]?pqt /
		 * 5 / 5.99               -> / \d+[[:space:]]?\/[[:space:]]\d+.\d{2} /
		 * 24.99 / caisse         -> / \d+.\d{2}[[:space:]]?\/[[:space:]]?caisse /
		 * 5.99 / lb 13.58 / kg   -> \d+.\d{2}[[:space:]]?\/[[:space:]]?lb[[:space:]]?\d+.\d{2}[[:space:]]?\/[[:space:]]?kg
		 * 
		 * */
		// Patterns des prix
		Pattern priceCH = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?ch");
		Pattern priceSac = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?sac");
		Pattern pricePqt = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?pqt");
		Pattern priceMulti = Pattern.compile("\\d+[\\s]?\\/[\\s]\\d+[.]\\d{2}");
		Pattern priceCaisse = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?caisse");
		Pattern pricePoid = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?lb[\\s]?\\d+[.]\\d{2}[\\s]?\\/[\\s]?kg");
		
		
		int id = -1;
		String description = "Non disponible";
		String format = "Non disponible";
		String origin = "Non disponible";
		double price = 0;
		int qty = 0;
		double rebate = 0;
		int rebate_percent = 0;
		String start = "Non disponible";
		String end = "Non disponible";
		String imgLink = "Non disponible";
		
		// Id
		if(element.select("a.dslink") != null){
			if(element.select("a.dslink").first().attr("onClick") != null){
				String rawId = element.select("a.dslink").first().attr("onClick");
				int indexOfIdEQ = rawId.indexOf("id=") + 3;
				String sId = "";
				for(int i = indexOfIdEQ; i<rawId.length(); i++){
					char charAt = rawId.charAt(i);
					
					if(charAt >= '0' && charAt <= '9'){
						sId += charAt;
					}
					else{
						break;
					}
				}
				id = Integer.parseInt(sId);
			}
		}
		// Description
		if(element.select("[width=230] > a, [width=228] > a").first() != null){
			description = element.select("[width=230] > a, [width=228] > a").first().text();
		}
		// Format
		if(element.select("td[width=76]").first() != null){
			format = element.select("td[width=76]").first().text();
		}
		// Origine
		if(element.select("td[width=92]").first() != null){
			origin = element.select("td[width=92]").first().text();
		}
		// Prix
		if(element.select("td[width=72]").first() != null){
			String priceUnitS = element.select("td[width=72]").first().text();
			// Matchers
			Matcher mPriceCH = priceCH.matcher(priceUnitS);
			Matcher mPricePqt = pricePqt.matcher(priceUnitS);
			Matcher mPriceSac = priceSac.matcher(priceUnitS);
			Matcher mPriceMulti = priceMulti.matcher(priceUnitS);
			Matcher mPriceCaisse = priceCaisse.matcher(priceUnitS);
			Matcher mPricePoid = pricePoid.matcher(priceUnitS);
			//Traitement en fonction du type d'item
			if(mPriceCH.find()){
				qty = 1;
				priceUnitS = mPriceCH.group(0);
				String priceS = priceUnitS.split("/")[0].trim();
				price = Double.parseDouble(priceS);
				
			}
			else if(mPricePqt.find()){
				qty = Product.QTY_PQT;
				priceUnitS = mPricePqt.group(0);
				String priceS = priceUnitS.split("/")[0].trim();
				price = Double.parseDouble(priceS);
			}
			else if(mPriceSac.find()){
				qty = Product.QTY_SAC;
				priceUnitS = mPriceSac.group(0);
				String priceS = priceUnitS.split("/")[0].trim();
				price = Double.parseDouble(priceS);
			}
			else if(mPriceMulti.find()){
				priceUnitS = mPriceMulti.group(0);
				String[] split = priceUnitS.split("/");
				qty = Integer.parseInt(split[0].trim());
				price = Double.parseDouble(split[1].trim());
			}
			else if(mPriceCaisse.find()){
				qty = Product.QTY_CAISSE;
				priceUnitS = mPriceCaisse.group(0);
				String priceS = priceUnitS.split("/")[0].trim();
				price = Double.parseDouble(priceS);
			}
			else if(mPricePoid.find()){
				qty = Product.QTY_POIDS;
				priceUnitS = mPricePoid.group(0);
				String priceS = priceUnitS.split("/")[0].trim();
				price = Double.parseDouble(priceS);
			}
		}
		// Rabais
		if(element.select("td[width=65]").first() != null){
			String[] splitted = element.select("td[width=65]").first().text().split("[$]");
			
			// Rabais en dollars
			String sRebate = "";
			for(int i = 0; i<splitted[0].length(); i++){
				char charAt = splitted[0].charAt(i);
				
				if((charAt >= '0' && charAt <= '9') || charAt == '.'){
					sRebate += charAt;
				}
			}
			rebate = Double.parseDouble(sRebate);
			// Rabais en pourcent
			String sRebatePer = "";
			for(int i = 0; i<splitted[1].length(); i++){
				char charAt = splitted[1].charAt(i);
				
				if(charAt >= '0' && charAt <= '9'){
					sRebatePer += charAt;
				}
			}
			rebate_percent = Integer.parseInt(sRebatePer);
		}
		// Dates
		if(element.select("td[width=60]").first() != null){
			String bothDates = element.select("td[width=60]").first().html();
			bothDates = bothDates.replace("&nbsp;", " ");
			String[] dateArray = bothDates.split("<br>");
			start = dateArray[0];
			end = dateArray[1];			
		}
		// Image
		if(element.select("img").first() != null){
			imgLink = element.select("img").first().attr("src");
		}
		
		
		return new Product(id, description, format, origin, price, qty, rebate,rebate_percent, start, end, imgLink);
	}
}
