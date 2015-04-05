package com.olivierboucher.ear;

import com.olivierboucher.crawler.CrawlerFactory;
import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.crawler.supermarches.SMCrawler;
import com.olivierboucher.crawler.walmart.WMCrawler;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieStore;
import com.olivierboucher.model.supermarches.SMEpicerieCategory;
import com.olivierboucher.model.supermarches.SMEpicerieStore;
import com.olivierboucher.model.walmart.WMEpicerieCategory;
import com.olivierboucher.model.walmart.WMEpicerieStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLHelper {


	private Connection connection;

	public void Connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://104.131.203.247/epicerie_a_rabais_v3", "ear_system", "EarSystem2015");
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public void Disconnect(){
		try {
			connection.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void SendProductList(List<EpicerieProduct> productList, int websiteId) throws SQLException{
		PreparedStatement resetStatement = connection.prepareStatement("UPDATE product_price AS price INNER JOIN product ON price.product_id = product.product_id INNER JOIN product_store AS store ON product_store_id = store.store_id SET price.price_active = 0 WHERE store.website_id = ?");
		resetStatement.setInt(1, websiteId);
		resetStatement.executeUpdate();

		PreparedStatement rqst = connection.prepareStatement("CALL InsertProductAndRebate(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		for(EpicerieProduct p : productList){
			rqst.setLong(1, p.getSku());
			rqst.setString(2, p.getDescription());
			rqst.setString(3, p.getSize());
			rqst.setString(4, p.getOrigin());
			rqst.setString(5, p.getThumbnail());
			rqst.setInt(6, p.getCategory().getProductCategoryId());
			rqst.setInt(7, p.getStore().getId());
			rqst.setDouble(8, p.getRebate().getPrice());
			rqst.setDouble(9,p.getRebate().getRebate());
			rqst.setDouble(10,p.getRebate().getRebate_percent());
			rqst.setInt(11, p.getRebate().getQuantity());
			rqst.setDate(12, new java.sql.Date(p.getRebate().getStart().getTime()));
			rqst.setString(13, p.getNote());
			rqst.setDate(14,new java.sql.Date(p.getRebate().getEnd().getTime()));

			// Ajouter à la batch
			rqst.addBatch();
		}
		for(int r:rqst.executeBatch()){
			int i = 0;
			if(r == Statement.EXECUTE_FAILED){
				System.out.print("Failure while adding product with sku "+ productList.get(i).getSku() + "\n");
			}
			i++;
		}
		rqst.close();
	}
	public Date SMGetActualStartDate() throws SQLException{
		//Récupèrer le premier resultat actif
		Statement rqst = connection.createStatement();
		ResultSet result = rqst.executeQuery("SELECT price_start FROM product_price WHERE price_active=1 LIMIT 1;");
		rqst.closeOnCompletion();
		if(result.next()){
			return result.getDate("price_start");
		}
		else{
			return null;
		}
	}
	public List<EpicerieCategory> GetCategoryList(int websiteId) throws SQLException{
		List<EpicerieCategory> list = new ArrayList<EpicerieCategory>();
		PreparedStatement rqst = connection.prepareStatement("SELECT * FROM website_category WHERE website_id=?");
		rqst.setInt(1,websiteId);
		ResultSet result = rqst.executeQuery();
		rqst.closeOnCompletion();
		switch(websiteId){
			case SMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new SMEpicerieCategory(result.getInt("category_id"), result.getString("category_name"), result.getString("category_slug"), result.getInt("product_category_id")));
				}
				break;
			case WMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new WMEpicerieCategory(result.getInt("category_id"), result.getString("category_name"), result.getString("category_slug"), result.getInt("product_category_id")));
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid website ID");
		}
		return list;
	}
	public List<EpicerieStore> GetStoreList(int websiteId) throws SQLException{
		List<EpicerieStore> list = new ArrayList<EpicerieStore>();
		PreparedStatement rqst = connection.prepareStatement("SELECT * FROM product_store WHERE website_id=?");
		rqst.setInt(1,websiteId);
		ResultSet result = rqst.executeQuery();
		rqst.closeOnCompletion();
		switch(websiteId){
			case SMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new SMEpicerieStore(result.getInt("store_id"), result.getString("store_name"), result.getString("store_slug")));
				}
				break;
			case WMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new WMEpicerieStore(result.getInt("store_id"), result.getString("store_name")));
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid website ID");
		}
		return list;
	}
	public List<EpicerieCrawler> GetCrawlers() throws SQLException{
		this.Connect();
		List<EpicerieCrawler> crawlers = new ArrayList<EpicerieCrawler>();
		PreparedStatement rqst = connection.prepareStatement("SELECT * FROM crawler WHERE enabled=1");
		ResultSet result = rqst.executeQuery();
		rqst.closeOnCompletion();
		while(result.next()){
			EpicerieCrawler crawler = CrawlerFactory.GetCrawler(result.getInt("website_id"));
			crawler.setExecMultiThreaded(result.getInt("multiThread") == 1 ? true : false);
			crawlers.add(crawler);
		}
		this.Disconnect();
		return crawlers;
	}
}
