package com.olivierboucher.ear;
import com.olivierboucher.crawler.*;

import com.olivierboucher.parser.impl.SMEpicerieParser;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CrawlerEpicerie extends Crawler<Product> {
	
	private List<Vendor> vendors;
	private List<Category> categories;
	private List<Product> products;
	private Common.CrawlerResult result;
	private SMEpicerieParser parser;
	
	public CrawlerEpicerie(){
		parser = new SMEpicerieParser();
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
		parser.setElement(element);
		return parser.getObject();
	}
}
